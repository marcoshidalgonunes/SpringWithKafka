package com.bank.account.transactions.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nio {
    String application;

    String terminal;

    LocalDate date;

    String time;
}