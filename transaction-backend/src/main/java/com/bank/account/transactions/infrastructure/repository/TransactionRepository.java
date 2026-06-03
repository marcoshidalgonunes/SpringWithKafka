package com.bank.account.transactions.infrastructure.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.bank.account.transactions.domain.model.Transaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@Repository
public class TransactionRepository {

    private final DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    public TransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Transaction getTransaction(String accountId, Integer transactionId) {
        String sql = "call get_transaction(?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, accountId);
            stmt.setInt(2, transactionId);
            stmt.registerOutParameter(3, Types.NUMERIC);
            stmt.registerOutParameter(4, Types.VARCHAR);

            stmt.execute();

            BigDecimal amount = stmt.getBigDecimal(3);
            String status = stmt.getString(4);

            if(amount == null) {
                log.warn("Transaction not found for transactionId={}", transactionId);
                return null;
            }

            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionId);
            transaction.setAmount(amount);
            transaction.setStatus(status);
            return transaction;
        } catch (Exception e) {
            log.error("Error calling get_transaction for transactionId={}", transactionId, e);
            return null;
        }
    }

    public Boolean createTransaction(String accountId, Integer transactionId, BigDecimal amount, String status) {
        String sql = "call create_transaction(?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, accountId);
            stmt.setInt(2, transactionId);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, status);
            stmt.execute();

            return true;
        } catch (Exception e) {
            log.error("Error calling create_transaction for accountId={}, transactionId={}", accountId, transactionId, e);
            return false;
        }
    }
}