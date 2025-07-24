package com.bank.account.transactions.engine.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.account.transactions.engine.IBalanceCalculator;

public class BalanceBlocked implements IBalanceCalculator {

    private final String accountId;

    private final String status;

    private static final Logger log = LoggerFactory.getLogger(BalanceBlocked.class);

    public BalanceBlocked(String status, String accountId) {
        this.status = status;
        this.accountId = accountId;
    }

    @Override
    public String execute(int transactionId, BigDecimal transactionValue) {
        log.warn("Transaction Id '{}' was not used for calculation", transactionId);
        return status;
    }

    @Override
    public void update() {
        log.warn("All calculations result for account Id '{}' was {}", accountId, status);
    }
}
