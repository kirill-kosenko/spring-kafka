package com.demo;

import com.demo.aggregates.CompanyAggregate;
import com.demo.events.BaseEvent;
import com.demo.serde.JSONSerde;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableKafkaStreams
public class SpringBootWithKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWithKafkaApplication.class, args);
	}

	@Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
	public StreamsConfig kStreamsConfigs() throws Exception {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "testStreams");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JSONSerde.class);
		props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
		props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
		return new StreamsConfig(props);
	}

	@Autowired
	ObjectMapper objectMapper;

//	@Bean
//	public KTable<String, String> companyAggregate(StreamsBuilder streamsBuilder) throws Exception {
//		KStream<String, String> stream = streamsBuilder.stream("event_company");
//		final KTable<String, String> companyAggregateTable = stream.groupByKey().aggregate(() -> "", this::aggregator,
//				Materialized.as("companyAggregate"));
//
//		stream.print(Printed.toSysOut());
//		return companyAggregateTable;
//	}
//
//	private String aggregator(final String key, final String value, final String aggregate) {
//		try {
////			Temporal dirty hack
//			Map<String, String> valueMap = objectMapper.readValue(value, Map.class);
//			Map<String, String> aggregateMap = StringUtils.isEmpty(aggregate)
//				? new HashMap<>()
//				:objectMapper.readValue(aggregate, Map.class);
//			aggregateMap.putAll(valueMap);
//			return objectMapper.writeValueAsString(aggregateMap);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return aggregate;
//	}


	@Bean
	public KTable<String, CompanyAggregate> companyAggregate(StreamsBuilder streamsBuilder) throws Exception {
		KStream<String, BaseEvent<String>> stream = streamsBuilder.stream("event_company");
		final KTable<String, CompanyAggregate> companyAggregateTable =
				stream.groupByKey()
						.aggregate(CompanyAggregate::new, this::aggregator, Materialized.as("companyAggregate"));


		stream.print(Printed.toSysOut());
		return companyAggregateTable;
	}

	private CompanyAggregate aggregator(final String key, final BaseEvent<String> value, final CompanyAggregate aggregate) {
//		try {
//			Temporal dirty hack
//			Map<String, String> valueMap = objectMapper.readValue(value, Map.class);
//			Map<String, String> aggregateMap = StringUtils.isEmpty(aggregate)
//					? new HashMap<>()
//					:objectMapper.readValue(aggregate, Map.class);
//			aggregateMap.putAll(valueMap);
			aggregate.apply(value);
			return aggregate;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return aggregate;
	}
}
//}
