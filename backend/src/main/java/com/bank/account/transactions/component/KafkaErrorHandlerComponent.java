package com.bank.account.transactions.component;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class KafkaErrorHandlerComponent {
    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlerComponent.class);

    private final KafkaTemplate<String, Object> kafkaTemplateObject;

    private final String deadLetterTopic;

    public KafkaErrorHandlerComponent(KafkaTemplate<String, Object> kafkaTemplateObject,
                                      @Value("${kafka.dead-letter-topic}") String deadLetterTopic) {
        this.kafkaTemplateObject = kafkaTemplateObject;
        this.deadLetterTopic = deadLetterTopic;
    }

    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler((record, exception) -> {
            log.error("Deserialization or processing error for record: {}", record, exception);
            // Send to a dead-letter topic
            if (record instanceof ConsumerRecord<?, ?>) {
                ConsumerRecord<?, ?> consumerRecord = (ConsumerRecord<?, ?>) record;
                Object keyObj = consumerRecord.key();
                Object valueObj = consumerRecord.value();
                if (keyObj instanceof String) {
                    String key = (String) keyObj;
                    kafkaTemplateObject.send(deadLetterTopic, key, valueObj);
                    log.info("Sent record to dead-letter topic: {}", consumerRecord);
                } else {
                    log.error("Cannot send to dead-letter topic: key has unexpected type. key={}", keyObj);
                }
            }
        });
    }
}