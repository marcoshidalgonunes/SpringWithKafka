package com.bank.account.transactions.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.bank.account.transactions.model.Transaction;


@Service
public class TransactionService {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    
    private final Map<String, CompletableFuture<Transaction>> futures = new ConcurrentHashMap<>();

    private final String producerTopic;

    private final String consumerTopic;

    private final int timeout;

    public TransactionService(
            KafkaTemplate<String, Transaction> kafkaTemplate,
            @Value("${kafka.producer-topic}") String producerTopic,
            @Value("${kafka.consumer-topic}") String consumerTopic,
            @Value("${kafka.timeout}") int timeout) {
        this.kafkaTemplate = kafkaTemplate;
        this.producerTopic = producerTopic;
        this.consumerTopic = consumerTopic;
        this.timeout = timeout;
    }

    @Retryable(
        value = { KafkaException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public Transaction sendAndReceive(Transaction payload) throws Exception, TimeoutException {
        log.info("Producing Transaction to Kafka: {}", payload);

        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Transaction> future = new CompletableFuture<>();
        // You will need to change the futures map to Map<String, CompletableFuture<Transaction>>
        futures.put(correlationId, future);

        ProducerRecord<String, Transaction> record = new ProducerRecord<>(producerTopic, correlationId, payload);
        record.headers().add("correlationId", correlationId.getBytes());
        record.headers().add("consumerTopic", consumerTopic.getBytes());

        try {
            kafkaTemplate.send(record).get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            futures.remove(correlationId);
            log.error("Kafka send failed", e.getCause());
            throw new RuntimeException("Kafka send failed: " + e.getCause().getMessage(), e.getCause());
        } catch (InterruptedException e) {
            futures.remove(correlationId);
            Thread.currentThread().interrupt();
            log.error("Kafka send interrupted", e);
            throw new RuntimeException("Kafka send interrupted", e);
        }

        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.error("Timeout while waiting for reply", e);
            futures.remove(correlationId);
            throw e;
        } catch (ExecutionException e) {
            log.error("Execution exception while waiting for reply", e);
            futures.remove(correlationId);
            return null;
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for reply", e);
            futures.remove(correlationId);
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            log.error("Unexpected exception while waiting for reply", e);
            futures.remove(correlationId);
            return null;
        }
    }

    @KafkaListener(topics = "${kafka.consumer-topic}", groupId = "${kafka.consumer-groupid}")
    public void listenReply(ConsumerRecord<String, Transaction> record) {
        log.info("Consumed Transaction from Kafka: {}", record.value());

        String correlationId = new String(record.headers().lastHeader("correlationId").value());
        CompletableFuture<Transaction> future = futures.remove(correlationId);
        if (future != null) {
            future.complete(record.value());
        }
    }
}
