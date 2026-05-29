package com.bank.account.transactions.application.engine.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.account.transactions.application.engine.IBalanceCalculator;
import com.bank.account.transactions.infrastructure.client.BalanceClient;

public class BalanceCalculator implements IBalanceCalculator {

    private static final Logger log = LoggerFactory.getLogger(BalanceCalculator.class);

    private final BalanceClient balanceClient;

    private final String accountId;

    private BigDecimal balance;

    public BalanceCalculator(BalanceClient balanceClient, String accountId, BigDecimal balance) {
        this.balanceClient = balanceClient;
        this.accountId = accountId;
        this.balance = balance;
    }

    @Override
    public String execute(int transactionId, BigDecimal transactionValue) {
        final BigDecimal updatedBalance = balance.add(transactionValue);
        if (updatedBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Transaction Id '{}' resulted in negative balance: {}", transactionId, updatedBalance);
            return "REJECTED";
        }

        balance = updatedBalance;
        log.info("Transaction Id '{}' updated balance = {}", transactionId, balance);

        return "ACCEPTED";
    }

    @Override
    public void update() {
        try {
            balanceClient.updateBalance(accountId, balance);
            log.info("Updated balance for account Id '{}' to {}", accountId, balance);
        } catch (Exception e) {
            log.error("Error updating balance for account Id {}: {}", accountId, e.getMessage(), e);
        }
    } 
}
