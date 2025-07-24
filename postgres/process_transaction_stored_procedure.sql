CREATE OR REPLACE FUNCTION process_transaction(
    p_accountId INTEGER,
    p_transactionId INTEGER,
    p_amount NUMERIC(18,2)
)
RETURNS VARCHAR(10) AS $$
DECLARE
    v_amount      NUMERIC(18,2);
    v_blocked      BOOLEAN;
    v_new_amount   NUMERIC(18,2);
    v_status       VARCHAR(10);
BEGIN
    -- Lock the row for update to ensure ACID
    SELECT amount, blocked
      INTO v_amount, v_blocked
      FROM balances
     WHERE accountId = p_accountId
     FOR UPDATE;

    IF NOT FOUND THEN
        v_status := 'INVALID';
        INSERT INTO transactions(accountId, transactionId, amount, status)
        VALUES (p_accountId, p_transactionId, p_amount, v_status);
        RETURN v_status;
    END IF;

    IF v_blocked THEN
        v_status := 'BLOCKED';
        INSERT INTO transactions(accountId, transactionId, amount, status)
        VALUES (p_accountId, p_transactionId, p_amount, v_status);
        RETURN v_status;
    END IF;

    v_new_amount := v_amount + p_amount;

    IF v_new_amount < 0 THEN
        v_status := 'REJECTED';

    ELSE
        BEGIN
            -- Start transaction block
            UPDATE balances
            SET amount = v_new_amount,
                updatedTimestamp = CURRENT_TIMESTAMP
            WHERE accountId = p_accountId;
            
            v_status := 'ACCEPTED';
            INSERT INTO transactions(accountId, transactionId, amount, status)
            VALUES (p_accountId, p_transactionId, p_amount, v_status);

            RETURN v_status;
        EXCEPTION WHEN OTHERS THEN
            -- Rollback happens automatically in plpgsql on error
            v_status := 'ERROR';
        END;
    END IF;
	
	INSERT INTO transactions(accountId, transactionId, amount, status)
    VALUES (p_accountId, p_transactionId, p_amount, v_status);
	
    RETURN v_status;
END;
$$ LANGUAGE plpgsql;