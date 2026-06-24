package com.bank.account.transactions.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Entry {
    Account account;

    String code;

    String description;

    BigDecimal amount;

    OffsetDateTime createdTimestamp;
}