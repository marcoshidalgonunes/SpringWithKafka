package com.bank.account.transactions.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    String branch;

    String number;

    @Override
    public String toString() {
        return branch + number;
    }
}