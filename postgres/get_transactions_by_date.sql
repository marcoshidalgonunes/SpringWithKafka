CREATE OR REPLACE FUNCTION get_transactions_by_date(
    p_accountId VARCHAR(16),
    p_date DATE
)
RETURNS TABLE (
    transactionId UUID,
    amount NUMERIC(18,2),
    code VARCHAR(4),
    description VARCHAR(40),
    status VARCHAR(10)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT t.transactionId, t.amount, t.code, t.description, t.status
    FROM transactions t
    WHERE t.accountId = p_accountId
      AND t.createdTimestamp >= p_date
      AND t.createdTimestamp < p_date + INTERVAL '1 day';
END;
$$;
