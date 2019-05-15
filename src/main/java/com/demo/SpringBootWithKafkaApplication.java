package com.demo;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SpringBootWithKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWithKafkaApplication.class, args);
	}


//	@Bean
//	public ProducerFactory<String, String> producerCommandFactory() {
//		final Map<String, Object> config = new HashMap<>();
//		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
//		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
//		config.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
//		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
//		config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "command-tx");
//		final DefaultKafkaProducerFactory<String, String> factory =
//				new DefaultKafkaProducerFactory<>(config);
//		factory.setTransactionIdPrefix("command-tx");
//		return factory;
//	}
//
//	@Bean
//	public KafkaTemplate<String, String> kafkaCommandTemplate(ProducerFactory<String, String> factory) {
//		return new KafkaTemplate<>(factory);
//	}

//	@Autowired
//	public ProducerFactory<String, String> producerFactory;
//
//	@Autowired
//	public ConsumerFactory<String, String> consumerFactory;
//
//	@Bean
//	public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory() {
//		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
//		factory.setConsumerFactory(consumerFactory);
//		factory.getContainerProperties().setAckOnError(false);
//		factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
//		factory.getContainerProperties().setErrorHandler((thrownException, data) -> System.out.println("#### Exception " + thrownException.getMessage()));
////		factory.setRetryTemplate(new RetryTemplate());
//		factory.getContainerProperties().setTransactionManager(new KafkaTransactionManager<>(producerFactory));
//
//		return factory;
//	}
}
