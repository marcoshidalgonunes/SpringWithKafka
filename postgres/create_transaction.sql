CREATE OR REPLACE PROCEDURE create_transaction(
    p_accountId VARCHAR(16),
    p_transactionId INTEGER,
    p_amount NUMERIC(18,2),
	p_status VARCHAR(10)
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO transactions (accountId, transactionId, amount, status)
	VALUES (p_accountId, p_transactionId, p_amount, p_status);
END;
$$;