CREATE OR REPLACE PROCEDURE update_balance(
    p_accountId VARCHAR(16),
    p_newAmount NUMERIC(18,2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Lock the row for update
    PERFORM 1 FROM balances WHERE accountId = p_accountId FOR UPDATE;

    -- Update the amount and timestamp
    UPDATE balances
       SET amount = p_newAmount,
           updatedTimestamp = CURRENT_TIMESTAMP
     WHERE accountId = p_accountId;
END;
$$;