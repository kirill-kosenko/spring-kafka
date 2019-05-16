package com.demo.engine;

import com.demo.commands.CreateCompanyCommand;
import com.demo.events.BaseEvent;
import com.demo.events.CompanyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class CompanyCommandHandler {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final KTable<String, String> comapnyAggregateTable;

    public CompanyCommandHandler(KafkaTemplate<Object, Object> kafkaTemplate, KTable<String, String> comapnyAggregateTable) {
        this.kafkaTemplate = kafkaTemplate;
        this.comapnyAggregateTable = comapnyAggregateTable;
    }

    @Transactional
    @KafkaListener(topics = "command_company")
    public void consumeCommandCompany(ConsumerRecord<?, ?> record) {
        log.info(String.format("command_company -> Consumed message -> %s", record.value().toString()));
        BaseEvent<?> event = convertToEvent(record.value());

        kafkaTemplate.send(new ProducerRecord<>("event_company", event.id, event));
        Map<TopicPartition, OffsetAndMetadata> map = Collections.singletonMap(
                new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
        kafkaTemplate.sendOffsetsToTransaction(map, "group_id");
        log.info("Messages sent");
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
