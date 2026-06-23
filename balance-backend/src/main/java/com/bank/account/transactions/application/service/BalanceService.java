package com.bank.account.transactions.application.service;

import com.bank.account.transactions.domain.model.Balance;
import com.bank.account.transactions.infrastructure.repository.BalanceRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public Balance getBalance(String accountId) {
        return balanceRepository.getBalance(accountId);
    }

    public Boolean updateBalance(String accountId, BigDecimal newAmount) {
        return balanceRepository.updateBalance(accountId, newAmount);
    }
}