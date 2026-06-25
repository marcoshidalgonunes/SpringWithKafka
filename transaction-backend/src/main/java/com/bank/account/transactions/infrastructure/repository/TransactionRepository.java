package com.bank.account.transactions.infrastructure.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.bank.account.transactions.domain.model.Entry;
import com.bank.account.transactions.domain.model.Transaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {

    private final DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    public TransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Transaction> getTransactionsByDate(String accountId, LocalDate date)  {

        String sql = "SELECT * FROM get_transactions_by_date(?, ?)";

        List<Transaction> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountId);
            stmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID transactionId = (UUID) rs.getObject("transactionId");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    String code = rs.getString("code");
                    String description = rs.getString("description");
                    String status = rs.getString("status");
                    OffsetDateTime createdTimestamp = rs.getObject("createdTimestamp", OffsetDateTime.class);

                    Entry entry = new Entry();
                    entry.setAmount(amount);
                    entry.setCode(code);
                    entry.setDescription(description);
                    entry.setCreatedTimestamp(createdTimestamp);

                    results.add(new Transaction(
                        transactionId,
                        entry,
                        status
                    ));
                }
            } 
        } catch (Exception e) {
            log.error("Error calling get_transactions_by_date for accountId={}", accountId, e);
            return Collections.emptyList();
        }

        return results;
    }

    public Boolean createTransaction(UUID transactionId, String accountId, BigDecimal amount, String code, String description, String status, OffsetDateTime createdTimestamp) {
        String sql = "call create_transaction(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
            CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setObject(1, transactionId);
            stmt.setString(2, accountId);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, code);
            stmt.setString(5, description);
            stmt.setString(6, status);
            stmt.setObject(7, createdTimestamp);
            stmt.execute();

            return true;
        } catch (Exception e) {
            log.error("Error calling create_transaction for accountId={}, transactionId={}", accountId, transactionId, e);
            return false;
        }
    }
}