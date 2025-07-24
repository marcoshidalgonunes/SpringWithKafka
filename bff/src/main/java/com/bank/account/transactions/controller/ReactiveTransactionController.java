package com.bank.account.transactions.controller;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.account.transactions.model.Transaction;
import com.bank.account.transactions.service.ReactiveTransactionService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/flux")
public class ReactiveTransactionController {

    private final ReactiveTransactionService transactionService;

    public ReactiveTransactionController(ReactiveTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/process")
    public Mono<ResponseEntity<Transaction>> process(@RequestBody Transaction payload) {
        return transactionService.processReactive(payload)
            .map(transaction -> ResponseEntity.ok(transaction))
            .onErrorResume(TimeoutException.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build()))
            .onErrorResume(Exception.class, e -> 
                Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
            .timeout(Duration.ofSeconds(30)) // Overall timeout for the endpoint
            .doOnError(e -> System.err.println("Error processing transaction: " + e.getMessage()));
    }
}
