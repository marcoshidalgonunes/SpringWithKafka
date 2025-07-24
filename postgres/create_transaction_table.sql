CREATE TABLE transactions (
    accountId INTEGER NOT NULL,
    transactionId INTEGER NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('PROCESS', 'ACCEPTED', 'REJECTED', 'BLOCKED', 'INVALID', 'ERROR')), // a processar, aceita, rejeitada por insuficiência de saldo, conta bloqueada, conta inexistente, erro de processamento
    createdTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (accountId, transactionId)
);
CREATE INDEX idx_transactions_accountid_createdtimestamp_desc
ON transactions (accountId, createdTimestamp DESC);