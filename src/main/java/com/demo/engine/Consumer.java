package com.demo.engine;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public Consumer(KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @KafkaListener(topics = "topic2", containerGroup = "group_id")
    public void consume(ConsumerRecord<?, ?> record) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", record.value()));
        kafkaTemplate.send(new ProducerRecord<>(record.topic(), record.value()));
        logger.info("Messages sent");
        Map<TopicPartition, OffsetAndMetadata> map = Collections.singletonMap(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));
        kafkaTemplate.sendOffsetsToTransaction(map, "group_id");
        //            if (true) throw new RuntimeException();
    }

    @KafkaListener(topics = "topic3")
    public void consumeTopic3(ConsumerRecord<?, ?> message) throws IOException {
        logger.info(String.format("#### -> Consumed message -> %s", message.value()));
    }
}
