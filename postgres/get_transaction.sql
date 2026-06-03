CREATE OR REPLACE PROCEDURE get_transaction(
    IN p_accountId VARCHAR(16),
    IN p_transactionId INTEGER,
    OUT p_amount NUMERIC(18,2),
	OUT p_status VARCHAR(10)
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT amount, status INTO p_amount, p_status FROM transactions WHERE accountId = p_accountId AND transactionId = p_transactionId;
END;
$$;
