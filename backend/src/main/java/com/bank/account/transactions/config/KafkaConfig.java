package com.bank.account.transactions.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.bank.account.transactions.model.Transaction;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.producer-topic}")
    private String producerTopic;

    @Value("${kafka.consumer-topic}")
    private String consumerTopic;

    @Bean
    ProducerFactory<String, Transaction> transactionProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, Transaction> transactionKafkaTemplate(ProducerFactory<String, Transaction> transactionProducerFactory) {
        return new KafkaTemplate<>(transactionProducerFactory);
    }

    @Bean
    ProducerFactory<String, Object> objectProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplateObject(ProducerFactory<String, Object> objectProducerFactory) {
        return new KafkaTemplate<>(objectProducerFactory);
    }
    
    @Bean
    ConsumerFactory<String, Transaction> transactionConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            new JsonDeserializer<>(Transaction.class, false)
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListenerContainerFactory(
            ConsumerFactory<String, Transaction> transactionConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Transaction> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory);
        return factory;
    }

    @Bean
    RetryTopicConfiguration retryConfig(KafkaTemplate<String, Transaction> kafkaTemplate) {
        return RetryTopicConfigurationBuilder
            .newInstance()
            .maxAttempts(3)
            .fixedBackOff(2000)
            .create(kafkaTemplate);
    }

    @Bean
    NewTopic requestTopic() {
        return TopicBuilder.name(producerTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic replyTopic() {
        return TopicBuilder.name(consumerTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }    
}
