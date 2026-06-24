package com.bank.account.transactions.application.engine;

import java.math.BigDecimal;
import java.util.UUID;

public interface IBalanceCalculator {
    String execute(UUID transactionId, BigDecimal transactionValue);

    void update();
}
