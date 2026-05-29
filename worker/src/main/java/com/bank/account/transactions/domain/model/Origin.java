package com.bank.account.transactions.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Origin {
    String entity;

    String branch;

    String userId;

    String cashier;

    String terminal;

    String channel;

    Boolean isAccounting = false;
}