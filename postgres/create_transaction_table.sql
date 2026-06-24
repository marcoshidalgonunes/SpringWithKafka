CREATE TABLE transactions (
    transactionId UUID NOT NULL,
    accountId VARCHAR(16) NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    code VARCHAR(4) NOT NULL,
    description VARCHAR(40) NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('PROCESS', 'ACCEPTED', 'REJECTED', 'BLOCKED', 'INVALID', 'ERROR')), // a processar, aceita, rejeitada por insuficiência de saldo, conta bloqueada, conta inexistente, erro de processamento
    createdTimestamp TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (transactionId),
    CONSTRAINT fk_transactions_accountid FOREIGN KEY (accountId) REFERENCES balances(accountId)
);
CREATE INDEX idx_transactions_accountid_createdtimestamp_desc
ON transactions (accountId, createdTimestamp DESC);