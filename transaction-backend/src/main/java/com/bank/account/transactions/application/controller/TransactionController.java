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

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountId}/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String accountId, @PathVariable Integer transactionId) {
        Transaction transaction = transactionService.getTransaction(accountId, transactionId);
        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transaction);
    }

    @PostMapping()
    public ResponseEntity<Void> createTransaction( @RequestBody Transaction transaction) {
        Boolean success = transactionService.createTransaction(transaction.getAccount().toString(), transaction.getTransactionId(), transaction.getAmount(), transaction.getStatus());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
