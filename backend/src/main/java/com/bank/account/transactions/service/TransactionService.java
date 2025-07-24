package com.bank.account.transactions.service;

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
import org.springframework.stereotype.Service;

import com.bank.account.transactions.model.Transaction;
import com.bank.account.transactions.repository.BalanceRepository;

@Service
public class TransactionService {

    private final KafkaTemplate<String, Transaction> transactionKafkaTemplate;
    private final BalanceRepository balanceRepository;

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    
    private final String producerTopic;

    public TransactionService(
            KafkaTemplate<String, Transaction> transactionKafkaTemplate,
            @Value("${kafka.producer-topic}") String producerTopic,
            BalanceRepository balanceRepository) {
        this.transactionKafkaTemplate = transactionKafkaTemplate;
        this.producerTopic = producerTopic;
        this.balanceRepository = balanceRepository;
    }

    @KafkaListener(topics = "${kafka.consumer-topic}", groupId = "${kafka.consumer-groupid}")
    public void consumeAndProcess(ConsumerRecord<String, Transaction> consumedRecord) {
        String key = consumedRecord.key(); // Get the key from the received message
        Transaction transaction = consumedRecord.value(); // Get the payload from the received message
        
		log.info("Consumed payload from Kafka: {}", transaction); // Log consumer payload

        // Process the payload
        Transaction processedResult = process(transaction);

        // Optionally, preserve correlationId if present in headers
        String correlationId = null;
        if (consumedRecord.headers().lastHeader("correlationId") != null) {
            correlationId = new String(consumedRecord.headers().lastHeader("correlationId").value());
        }

        ProducerRecord<String, Transaction> produceRecord = new ProducerRecord<>(producerTopic, key, processedResult);
        if (correlationId != null) {
            produceRecord.headers().add("correlationId", correlationId.getBytes());
        }

        log.info("Producing payload to Kafka with key {}: {}", key, processedResult);
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

    private Transaction process(Transaction transaction) {
        // Call stored procedure to process transaction via BalanceRepository
        String status = balanceRepository.processTransaction(
            transaction.getAccountId(),
            transaction.getTransactionId(), 
            transaction.getAmount()
        );
        transaction.setStatus(status);
        return transaction;
    }
}