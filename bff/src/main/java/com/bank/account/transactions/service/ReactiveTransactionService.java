package com.bank.account.transactions.service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;

import com.bank.account.transactions.model.Transaction;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
public class ReactiveTransactionService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveTransactionService.class);

    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final ReactiveKafkaConsumerTemplate<String, Transaction> replyConsumer;
    private final String producerTopic;
    private final int timeout;

    // Correlation map for request-reply
    private final Map<String, Sinks.One<Transaction>> pendingReplies = new ConcurrentHashMap<>();

    public ReactiveTransactionService(
            KafkaTemplate<String, Transaction> kafkaTemplate,
            ReactiveKafkaConsumerTemplate<String, Transaction> replyConsumer,
            @Value("${kafka.producer-topic}") String producerTopic,
            @Value("${kafka.timeout:30000}") int timeout) {
        this.kafkaTemplate = kafkaTemplate;
        this.replyConsumer = replyConsumer;
        this.producerTopic = producerTopic;
        this.timeout = timeout;
        startReplyListener();
    }

    /**
     * Send transaction and return Mono that completes when reply is received from reply topic.
     */
    public Mono<Transaction> processReactive(Transaction transaction) {
        String correlationId = UUID.randomUUID().toString();
        Sinks.One<Transaction> sink = Sinks.one();
        pendingReplies.put(correlationId, sink);

        return publishToKafka(correlationId, transaction)
            .then(sink.asMono()
                .timeout(Duration.ofMillis(timeout))
                .doFinally(sig -> pendingReplies.remove(correlationId))
            )
            .doOnSuccess(result -> log.info("Transaction processed successfully: {}", correlationId))
            .doOnError(error -> log.error("Error processing transaction: {}", correlationId, error));
    }

    private Mono<Void> publishToKafka(String correlationId, Transaction transaction) {
        return Mono.fromCallable(() -> {
            ProducerRecord<String, Transaction> record = new ProducerRecord<>(producerTopic, correlationId, transaction);
            record.headers().add("correlationId", correlationId.getBytes());
            log.info("Producing Transaction to Kafka: {}", transaction);
            kafkaTemplate.send(record).get(timeout, java.util.concurrent.TimeUnit.MILLISECONDS);
            return null;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then()
        .doOnSuccess(v -> log.info("Successfully published transaction to Kafka: {}", correlationId))
        .doOnError(error -> log.error("Failed to publish transaction to Kafka: {}", correlationId, error));
    }

    /**
     * Start a reactive consumer for the reply topic.
     */
    private void startReplyListener() {
        replyConsumer
            .receiveAutoAck()
            .doOnNext(record -> log.info("Consumed Transaction from reply topic: {}", record.value()))
            .flatMap(this::handleReply)
            .subscribe();
    }

    private Mono<Void> handleReply(ConsumerRecord<String, Transaction> record) {
        return Mono.fromRunnable(() -> {
            Header header = record.headers().lastHeader("correlationId");
            if (header != null) {
                String correlationId = new String(header.value());
                Sinks.One<Transaction> sink = pendingReplies.remove(correlationId);
                if (sink != null) {
                    sink.tryEmitValue(record.value());
                } else {
                    log.warn("No pending request for correlationId={}", correlationId);
                }
            } else {
                log.warn("No correlationId header found in reply");
            }
        });
    }
}