package com.infy.eonservice.config;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.SendTo;


@EnableBinding(MultipleProcessor.class)
public class KafkaStreams {
		
		@StreamListener("eonservice-source")
		@SendTo("eonservice-destination")
		public KStream<Object,String> process(KStream<Object, String> input) {
			return input;
//			return input
//					.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
//					.map((key, value) -> new KeyValue<>(value, value))
//					.groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
//					.windowedBy(TimeWindows.of(60_000))
//					.count(Materialized.as("WordCounts-1"))
//					.toStream()
//					.map((key, value) -> new KeyValue<>("hello", new WordCount(key.key(), (long)10, new Date(), new Date())));
				}
	}

	interface MultipleProcessor {

		String BINDING_2 = "eonservice-source";
		String OUTPUT = "eonservice-destination";


		@Input(BINDING_2)
		KStream<?, ?> binding2();

		@Output(OUTPUT)
		KStream<?, ?> singleOutput();
	}



