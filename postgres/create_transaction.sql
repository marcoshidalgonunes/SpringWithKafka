CREATE OR REPLACE PROCEDURE create_transaction(
    p_transactionId UUID,
    p_accountId VARCHAR(16),
    p_amount NUMERIC(18,2),
    p_code VARCHAR(4),
    p_description VARCHAR(40),
	p_status VARCHAR(10),
    p_createdTimestamp TIMESTAMPTZ
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO transactions (transactionId, accountId, amount, code, description, status, createdTimestamp)
	VALUES (p_transactionId, p_accountId, p_amount, p_code, p_description, p_status, p_createdTimestamp);
END;
$$;