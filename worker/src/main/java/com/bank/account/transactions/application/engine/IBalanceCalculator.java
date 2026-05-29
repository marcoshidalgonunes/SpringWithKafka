package com.bank.account.transactions.application.engine;

import java.math.BigDecimal;

public interface IBalanceCalculator {
    String execute(int transactionId, BigDecimal transactionValue);

    void update();
}
