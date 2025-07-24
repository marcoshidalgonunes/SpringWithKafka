package com.bank.account.transactions.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.bank.account.transactions.model.Balance;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@Repository
public class BalanceRepository {

    private final DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(BalanceRepository.class);

    public BalanceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Balance getBalance(String accountId) {
        String sql = "call get_balance(?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, accountId);
            stmt.registerOutParameter(2, Types.NUMERIC);
            stmt.registerOutParameter(3, Types.BOOLEAN);

            stmt.execute();

            BigDecimal amount = stmt.getBigDecimal(2);
            boolean blocked = stmt.getBoolean(3);

            return new Balance(accountId, amount, blocked);
        } catch (Exception e) {
            log.error("Error calling get_balance for accountId={}", accountId, e);
            return null;
        }
    }

    public Boolean updateBalance(String accountId, BigDecimal newAmount) {
        String sql = "call update_balance(?, ?)";
        try (Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, accountId);
            stmt.setBigDecimal(2, newAmount);

            stmt.execute();

            return true;
        } catch (Exception e) {
            log.error("Error calling update_balance for accountId={}", accountId, e);
            return false;
        }
    }
}