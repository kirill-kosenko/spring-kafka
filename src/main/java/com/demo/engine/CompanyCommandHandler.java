package com.demo.engine;

import com.demo.aggregates.CompanyAggregate;
import com.demo.commands.BaseCommand;
import com.demo.commands.CreateCompanyCommand;
import com.demo.commands.UpdateCompanyCommand;
import com.demo.dto.commands.ValidationResult;
import com.demo.events.BaseEvent;
import com.demo.events.CompanyCreatedEvent;
import com.demo.events.CompanyUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class CompanyCommandHandler {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public CompanyCommandHandler(KafkaTemplate<Object, Object> kafkaTemplate,
                                 StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.kafkaTemplate = kafkaTemplate;
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    @Transactional
    @KafkaListener(topics = "command_company")
    public void consumeCommandCompany(ConsumerRecord<String, BaseCommand<String>> record) throws InterruptedException {
        log.info(String.format("command_company -> Consumed message -> %s", record.value().toString()));
        CompanyAggregate aggregate = getCompanyAggregate(record.key());
        ValidationResult validationResult = aggregate.validate(record.value());
        if (validationResult.isValid) {
            BaseEvent<String> event = convertToEvent(record.value());
            kafkaTemplate.send("event_company", event.id, event);
            log.info("Messages sent");
        }

        kafkaTemplate.send("command_company_validation_result", validationResult.id, validationResult);
        Map<TopicPartition, OffsetAndMetadata> topicOffset = Collections.singletonMap(
                new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
        kafkaTemplate.sendOffsetsToTransaction(topicOffset, "group_id");
    }

    private CompanyAggregate getCompanyAggregate(String key) throws InterruptedException {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();

        final ReadOnlyKeyValueStore<String, CompanyAggregate> companyStore = getStore(kafkaStreams, "companyAggregate");
        CompanyAggregate aggregate = companyStore.get(key);
        return  null == aggregate ? new CompanyAggregate() : aggregate;
    }

    private ReadOnlyKeyValueStore<String, CompanyAggregate> getStore(KafkaStreams kafkaStreams, String storeName) throws InterruptedException {
        // There is possibility KafkaStreams is not in RUNNING state during startup
        while (true) {
            try {
                return kafkaStreams.store(storeName, QueryableStoreTypes.keyValueStore());
            } catch (InvalidStateStoreException ex) {
                Thread.sleep(100);
            }
        }
    }

    private BaseEvent<String> convertToEvent(BaseCommand<String> command) {
        if (command instanceof CreateCompanyCommand) {
            CreateCompanyCommand createCompanyCommand = (CreateCompanyCommand) command;
            return new CompanyCreatedEvent(createCompanyCommand.id, createCompanyCommand.name, createCompanyCommand.address);
        } else if (command instanceof UpdateCompanyCommand) {
            UpdateCompanyCommand updateCompanyCommand = (UpdateCompanyCommand) command;
            return new CompanyUpdatedEvent(updateCompanyCommand.id, updateCompanyCommand.name, updateCompanyCommand.address);
        }
        return null;
    }

    @KafkaListener(topics = "event_company")
    public void consumeEventCompany(ConsumerRecord<?, ?> message, Acknowledgment ack) {
        log.info(String.format("event_company -> Consumed message -> %s", message.value()));
        ack.acknowledge();
    }
}
