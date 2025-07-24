package com.bank.account.transactions.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    Integer accountId;

    Integer transactionId;

    BigDecimal amount;
    
    String status;
}
