package com.bank.account.transactions.application.service;

import com.bank.account.transactions.domain.model.Account;
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
        Account account = new Account();
        account.setBranch(accountId.substring(0, 4));
        account.setNumber(accountId.substring(4));

        Transaction transaction = transactionRepository.getTransaction(accountId, transactionId);
        transaction.setAccount(account);
        
        return transaction;
    }

    public Boolean createTransaction(String accountId, Integer transactionId, BigDecimal amount, String status) {
        return transactionRepository.createTransaction(accountId, transactionId, amount, status);
    }
}