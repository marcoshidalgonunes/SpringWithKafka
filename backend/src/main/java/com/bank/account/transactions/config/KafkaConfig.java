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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.lang.NonNull;

import com.bank.account.transactions.model.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class KafkaConfig {

    private final @NonNull String producerTopic;
    private final @NonNull String consumerTopic;

    public KafkaConfig(
            @Value("${kafka.producer-topic}") String producerTopic,
            @Value("${kafka.consumer-topic}") String consumerTopic) {
        this.producerTopic = Objects.requireNonNull(producerTopic, "producerTopic must not be null");
        this.consumerTopic = Objects.requireNonNull(consumerTopic, "consumerTopic must not be null");
    }

    @Bean
    ProducerFactory<String, Transaction> transactionProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Objects.requireNonNull(bootstrapServers, "bootstrapServers must not be null"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, Transaction> transactionKafkaTemplate(ProducerFactory<String, Transaction> transactionProducerFactory) {
        return new KafkaTemplate<>(Objects.requireNonNull(transactionProducerFactory, "transactionProducerFactory must not be null"));
    }

    @Bean
    ProducerFactory<String, Object> objectProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Objects.requireNonNull(bootstrapServers, "bootstrapServers must not be null"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplateObject(ProducerFactory<String, Object> objectProducerFactory) {
        return new KafkaTemplate<>(Objects.requireNonNull(objectProducerFactory, "objectProducerFactory must not be null"));
    }

    @Bean
    ConsumerFactory<String, Transaction> transactionConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Objects.requireNonNull(bootstrapServers, "bootstrapServers must not be null"));
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
        factory.setConsumerFactory(Objects.requireNonNull(transactionConsumerFactory, "transactionConsumerFactory must not be null"));
        return factory;
    }

    @Bean
    RetryTopicConfiguration retryConfig(KafkaTemplate<String, Transaction> kafkaTemplate) {
        return RetryTopicConfigurationBuilder
            .newInstance()
            .maxAttempts(3)
            .fixedBackOff(2000)
            .create(Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null"));
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
