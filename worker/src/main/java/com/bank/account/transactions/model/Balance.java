package com.bank.account.transactions.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
    String accountId;

    BigDecimal amount;

    Boolean blocked = false;
}
