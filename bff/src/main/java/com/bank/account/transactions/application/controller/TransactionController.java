package com.bank.account.transactions.application.controller;

import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.account.transactions.domain.model.Transaction;
import com.bank.account.transactions.infrastructure.messages.TransactionMessaging;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionMessaging transactionService;

    public TransactionController(TransactionMessaging transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/process")
    public ResponseEntity<Transaction> process(@RequestBody Transaction payload) throws Exception {
        try {
            Transaction transaction = transactionService.sendAndReceive(payload);
            if (transaction == null) {
                return ResponseEntity.status(500).build();
            }
            return ResponseEntity.ok().body(transaction);
        } catch (TimeoutException e) {
            // Timeout occurred after retries
            return ResponseEntity.status(504).build();
        } 
    }
}