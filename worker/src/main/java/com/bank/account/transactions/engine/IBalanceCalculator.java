package com.bank.account.transactions.engine;

import java.math.BigDecimal;

public interface IBalanceCalculator {
    String execute(int transactionId, BigDecimal transactionValue);

    void update();
}
