package com.bank.account.transactions.application.service;

import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bank.account.transactions.application.engine.BalanceCalculatorFactory;
import com.bank.account.transactions.application.engine.IBalanceCalculator;
import com.bank.account.transactions.domain.model.Transaction;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    
    private final String producerTopic;

    private final BalanceCalculatorFactory balanceCalculatorFactory;

    private final KafkaTemplate<String, Transaction> transactionKafkaTemplate;

    private final Object updateLock = new Object();

    private int transactionsCounter = 0;

    private Instant startBatchTimestamp = null;

    private IBalanceCalculator balanceCalculator = null;

    public TransactionService(
            KafkaTemplate<String, Transaction> transactionKafkaTemplate,
            @Value("${kafka.producer-topic}") String producerTopic,
            BalanceCalculatorFactory balanceCalculatorFactory) {
        this.transactionKafkaTemplate = transactionKafkaTemplate;
        this.producerTopic = producerTopic;
        this.balanceCalculatorFactory = balanceCalculatorFactory;
    }

    @KafkaListener(topics = "${kafka.consumer-topic}", groupId = "${kafka.consumer-groupid}")
    public void consumeAndProcess(ConsumerRecord<String, Transaction> consumedRecord) {
        String key = consumedRecord.key(); // Get the key from the received message
        Transaction transaction = consumedRecord.value(); // Get the payload from the received message
        
		log.info("Consumed payload from Kafka: {}", transaction); // Log consumer payload

        // Process the payload
        prepare(transaction);

        transactionsCounter++;
        final String status = balanceCalculator.execute(transaction.getTransactionId(), transaction.getEntry().getAmount());
        transaction.setStatus(status);

        // Send updated transaction to Kafka
        String correlationId = null;
        if (consumedRecord.headers().lastHeader("correlationId") != null) {
            correlationId = new String(consumedRecord.headers().lastHeader("correlationId").value());
        }

        ProducerRecord<String, Transaction> produceRecord = new ProducerRecord<>(producerTopic, key, transaction);
        if (correlationId != null) {
            produceRecord.headers().add("correlationId", correlationId.getBytes());
        }

        log.info("Producing payload to Kafka with key {}: {}", key, transaction);
        try {
            transactionKafkaTemplate.send(produceRecord).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Handle timeout (e.g., log and/or rethrow)
            log.error("Kafka send timed out", e);
        } catch (ExecutionException e) {
            // Handle send failure (e.g., broker unavailable)
            log.error("Kafka send failed", e);
        } catch (InterruptedException e) {
            // Handle thread interruption
            Thread.currentThread().interrupt();
            log.error("Kafka send interrupted", e);
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            log.error("Unexpected error during Kafka send", e);
        }
    }

    @Scheduled(fixedDelayString = "${spring.application.batch-interval:1000}")
    public void completeBatch() {
        synchronized (updateLock) {
            if (startBatchTimestamp != null) {
                long duration = Duration.between(startBatchTimestamp, Instant.now()).toMillis();
                if (transactionsCounter >= 10 || duration >= 500) {
                    log.info("Completing batch of {} transactions after {} ms", transactionsCounter, duration);
                    balanceCalculator.update();

                    // Reset the batch state
                    transactionsCounter = 0;
                    startBatchTimestamp = null;
                    balanceCalculator = null;
                }
            }
        }
    }

    private void prepare(Transaction transaction) {
        synchronized (updateLock) {
            if (transactionsCounter == 0) {
                balanceCalculator = balanceCalculatorFactory.create(transaction.getEntry().getAccount().toString());
                startBatchTimestamp = Instant.now();
            }
        }
    }
}