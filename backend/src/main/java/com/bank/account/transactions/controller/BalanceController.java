package com.bank.account.transactions.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.account.transactions.model.Balance;
import com.bank.account.transactions.service.BalanceService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/balance")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Balance> getBalance(@PathVariable String accountId) {
        Balance balance = balanceService.getBalance(accountId);
        if (balance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(balance);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Void> updateBalance(@PathVariable String accountId, @RequestBody BigDecimal newAmount) {
        Boolean success = balanceService.updateBalance(accountId, newAmount);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

}
