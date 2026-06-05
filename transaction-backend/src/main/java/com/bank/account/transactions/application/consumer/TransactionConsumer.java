package com.bank.account.transactions.application.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.bank.account.transactions.application.service.TransactionService;
import com.bank.account.transactions.domain.model.Transaction;

@Component
public class TransactionConsumer {

    private final TransactionService transactionService;

    public TransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    @KafkaListener(topics = "${kafka.consumer-topic}", groupId = "${kafka.consumer-groupid}")
    public void consumeAndProcess(ConsumerRecord<String, Transaction> consumedRecord) {
        Transaction transaction = consumedRecord.value(); // Get the payload from the received message

		log.info("Consumed payload from Kafka: {}", transaction); // Log consumer payload

        Boolean success = transactionService.createTransaction(transaction.getAccount().toString(), transaction.getTransactionId(), transaction.getAmount(), transaction.getStatus());
        if (!success) {
            log.error("Failed to process transaction: {}", transaction);
        }
    }
}
