package com.bank.account.transactions.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bank.account.transactions.engine.impl.BalanceBlocked;
import com.bank.account.transactions.engine.impl.BalanceCalculator;
import com.bank.account.transactions.model.Balance;
import com.bank.account.transactions.repository.BalanceRepository;

@Component
public class BalanceCalculatorFactory {

    private static final Logger log = LoggerFactory.getLogger(BalanceCalculatorFactory.class);

    private final BalanceRepository balanceRepository;

    public BalanceCalculatorFactory(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public IBalanceCalculator create(String accountId) {
        log.info("Retrieving balance for account Id '{}'", accountId);
        
        Balance balance = balanceRepository.getBalance(accountId);
        if (balance == null) {
            return new BalanceBlocked("ERROR", accountId);
        }
        if (balance.getAmount() == null) {
            return new BalanceBlocked("NOT FOUND", accountId);
        }
        if (balance.getBlocked()) {
            return new BalanceBlocked("BLOCKED", accountId);
        }

        return new BalanceCalculator(balanceRepository, accountId, balance.getAmount());
    }
}
