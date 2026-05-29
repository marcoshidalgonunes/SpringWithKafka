package com.bank.account.transactions.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Retention {
    String indicator = "00";

    String number;

    String code;

    BigDecimal amount;

    Integer validDays;

    LocalDate expirationDate;
    
    Boolean affectsLiquidation = false;
}