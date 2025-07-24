package com.bank.account.transactions.controller;

import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.account.transactions.model.Transaction;
import com.bank.account.transactions.service.TransactionService;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/process")
    public ResponseEntity<Transaction> process(@RequestBody Transaction payload) throws Exception {
        try {
            Transaction result = transactionService.sendAndReceive(payload);
            if (result == null) {
                return ResponseEntity.status(500).build();
            }
            return ResponseEntity.ok(result);
        } catch (TimeoutException e) {
            // Timeout occurred after retries
            return ResponseEntity.status(504).build();
        } 
    }
}