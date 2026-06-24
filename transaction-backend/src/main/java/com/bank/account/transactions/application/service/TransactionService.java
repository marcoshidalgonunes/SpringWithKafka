package com.bank.account.transactions.application.service;

import com.bank.account.transactions.domain.model.Entry;
import com.bank.account.transactions.domain.model.Transaction;
import com.bank.account.transactions.infrastructure.repository.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByDate(String accountId, String date) {
        try {
            return transactionRepository.getTransactionsByDate(accountId, LocalDate.parse(date));
        } catch (DateTimeParseException e) {
            log.error("invalid date {} to get transactions for accountId={}", date, accountId, e);
            return null;
        }
    }

    public Boolean createTransaction(Transaction transaction) {
        Entry entry = transaction.getEntry();
        return transactionRepository.createTransaction(transaction.getTransactionId(), 
            entry.getAccount().toString(), 
            entry.getAmount(), 
            entry.getCode(), 
            entry.getDescription(), 
            transaction.getStatus(), 
            entry.getCreatedTimestamp());
    }
}