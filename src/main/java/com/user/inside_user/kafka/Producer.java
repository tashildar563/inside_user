package com.user.inside_user.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
/**
 * The Producer class is responsible for sending messages to a Kafka topic.
 * It uses a KafkaTemplate to send messages.
 * The class uses field-based dependency injection to inject the KafkaTemplate.
 */
@Service
public class Producer {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    public void sendMessage(String message) {
        kafkaTemplate.send("user_event", message);
    }
}
