package com.demo.engine;

import com.demo.commands.CreateCompanyCommand;
import com.demo.events.BaseEvent;
import com.demo.events.CompanyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.StreamsBuilderFactoryBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = "command_company")
    public void consumeCommandCompany(ConsumerRecord<?, ?> record) throws InterruptedException {
        log.info(String.format("command_company -> Consumed message -> %s", record.value().toString()));
        BaseEvent<?> event = convertToEvent(record.value());
        getCompanyAggregate(record.key().toString());
        kafkaTemplate.send(new ProducerRecord<>("event_company", event.id, event));
        Map<TopicPartition, OffsetAndMetadata> map = Collections.singletonMap(
                new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
        kafkaTemplate.sendOffsetsToTransaction(map, "group_id");

        log.info("Messages sent");
//        throw new RuntimeException();Cv
    }

    private void getCompanyAggregate(String key) throws InterruptedException {
        KafkaStreams kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();

        final ReadOnlyKeyValueStore<String, String> companyStore = getStore(kafkaStreams, "companyAggregate");

        String aggregate = companyStore.get(key);
    }

    private ReadOnlyKeyValueStore<String, String> getStore(KafkaStreams kafkaStreams, String storeName) throws InterruptedException {
        // There is possibility KafkaStreams is not in RUNNING state during startup
        while (true) {
            try {
                return kafkaStreams.store(storeName, QueryableStoreTypes.keyValueStore());
            } catch (InvalidStateStoreException ex) {
                Thread.sleep(100);
            }
        }
    }

    private BaseEvent<?> convertToEvent(Object command) {
        if (command instanceof CreateCompanyCommand) {
            CreateCompanyCommand createCompanyCommand = (CreateCompanyCommand) command;
            return new CompanyCreatedEvent(createCompanyCommand.id, createCompanyCommand.name);
        }
        return null;
    }

    @KafkaListener(topics = "event_company")
    public void consumeEventCompany(ConsumerRecord<?, ?> message, Acknowledgment ack) {
        log.info(String.format("event_company -> Consumed message -> %s", message.value()));
        ack.acknowledge();
    }
}
