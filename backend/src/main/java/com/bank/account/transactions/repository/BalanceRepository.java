package com.bank.account.transactions.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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

    public String processTransaction(int accountId, int transactionId, BigDecimal amount) {
        String result = "ERROR";
        String sql = "{ ? = call process_transaction(?, ?, ?) }";
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.setInt(2, accountId);
            stmt.setInt(3, transactionId);
            stmt.setBigDecimal(4, amount);

            stmt.execute();
            result = stmt.getString(1);

        } catch (Exception e) {
            log.error("Error calling process_transaction", e);
        }
        return result;
    }
}