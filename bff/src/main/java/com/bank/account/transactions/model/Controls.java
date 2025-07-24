package com.bank.account.transactions.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Controls {
    String operationType;

    Boolean isRealOperation;

    String validationLevel;

    String transactionOrigin;

    String observationType;

    Boolean isPrincipal = true;

    Boolean isDebitToAvailable;

    Boolean shouldUseLimit = true;

    String timeDelay = "";

    Boolean isEscrowAccount = false;
    
    String provisionalType = "";
}