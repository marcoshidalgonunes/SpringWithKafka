package com.bank.account.transactions.engine.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.account.transactions.engine.IBalanceCalculator;
import com.bank.account.transactions.repository.BalanceRepository;

public class BalanceCalculator implements IBalanceCalculator {

    private static final Logger log = LoggerFactory.getLogger(BalanceCalculator.class);

    private final BalanceRepository balanceRepository;

    private final String accountId;

    private BigDecimal balance;

    public BalanceCalculator(BalanceRepository balanceRepository, String accountId, BigDecimal balance) {
        this.balanceRepository = balanceRepository;
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
        log.info("Updating balance for account Id '{}' to {}", accountId, balance);
        balanceRepository.updateBalance(accountId, balance);
    }
}
