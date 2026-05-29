package com.bank.account.transactions.application.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bank.account.transactions.application.engine.impl.BalanceBlocked;
import com.bank.account.transactions.application.engine.impl.BalanceCalculator;
import com.bank.account.transactions.domain.model.Balance;
import com.bank.account.transactions.infrastructure.client.BalanceClient;

@Component
public class BalanceCalculatorFactory {

    private static final Logger log = LoggerFactory.getLogger(BalanceCalculatorFactory.class);

    private final BalanceClient balanceClient;

    public BalanceCalculatorFactory(BalanceClient balanceClient) {
        this.balanceClient = balanceClient;
    }

    public IBalanceCalculator create(String accountId) {
        log.info("Retrieving balance for account Id '{}'", accountId);
        
        Balance balance = null;
        try {
            balance = balanceClient.getBalance(accountId);
        } catch (Exception e) {
            // Log the error and treat as error status
            log.error("Error retrieving balance for accountId={}", accountId, e);
            return new BalanceBlocked("ERROR", accountId);
        }
        if (balance == null || balance.getBlocked()) {
            String status = (balance == null) ? "INVALID" : "BLOCKED";
            return new BalanceBlocked(status, accountId);
        }

        return new BalanceCalculator(balanceClient, accountId, balance.getAmount());
    }
}
