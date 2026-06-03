package com.bank.account.transactions.application.service;

import com.bank.account.transactions.domain.model.Transaction;
import com.bank.account.transactions.infrastructure.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getTransaction(String accountId, Integer transactionId) {
        return transactionRepository.getTransaction(accountId, transactionId);
    }

    public Boolean createTransaction(String accountId, Integer transactionId, BigDecimal amount, String status) {
        return transactionRepository.createTransaction(accountId, transactionId, amount, status);
    }
}