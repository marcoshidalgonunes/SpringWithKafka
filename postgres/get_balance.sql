CREATE OR REPLACE PROCEDURE get_balance(
    IN p_accountId VARCHAR(16),
    OUT p_amount NUMERIC(18,2),
    OUT p_blocked BOOLEAN
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT amount, blocked INTO p_amount, p_blocked FROM balances WHERE accountId = p_accountId;
END;
$$;