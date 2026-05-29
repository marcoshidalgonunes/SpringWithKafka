package com.bank.account.transactions.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    Integer transactionId;

    Account account;

    Origin origin;

    String type;

    String code;

    BigDecimal amount;

    String concept;

    LocalDateTime timestamp;

    LocalDate accountingDate;

    String checkNumber;

    String internalReference;

    String observation;

    String historyComplement;

    Tax tax;

    Controls controls;

    Cancellation cancellation;

    Retention retention;

    Nio nio;

    String status;
}
