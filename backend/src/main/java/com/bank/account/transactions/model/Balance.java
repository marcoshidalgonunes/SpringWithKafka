package com.bank.account.transactions.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
    private String accountId;

    private BigDecimal amount;
    
    @Builder.Default
    private Boolean blocked = false;
}
