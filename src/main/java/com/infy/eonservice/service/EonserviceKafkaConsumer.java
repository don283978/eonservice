package com.infy.eonservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EonserviceKafkaConsumer {

    private final Logger log = LoggerFactory.getLogger(EonserviceKafkaConsumer.class);
    private static final String TOPIC = "topic_eonservice";

    @KafkaListener(topics = "topic_eonservice", groupId = "group_id")
    public void consume(String message) throws IOException {
        log.info("Consumed message in {} : {}", TOPIC, message);
    }
}
