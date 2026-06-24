package com.bank.account.transactions.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.account.transactions.application.service.TransactionService;
import com.bank.account.transactions.domain.model.Transaction;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountId}/{date}")
    public ResponseEntity<List<Transaction>> getTransaction(@PathVariable String accountId, @PathVariable String date) {
        List<Transaction> transactions = transactionService.getTransactionsByDate(accountId, date);
        if (transactions == null) {
            return ResponseEntity.badRequest().build();
        }
        else if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @PostMapping()
    public ResponseEntity<Void> createTransaction(@RequestBody Transaction transaction) {
        Boolean success = transactionService.createTransaction(transaction);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
