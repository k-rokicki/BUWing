CREATE OR REPLACE FUNCTION activate_account(_userid INTEGER, _token VARCHAR)
RETURNS TABLE (
    success BOOLEAN,
    name VARCHAR,
    surname VARCHAR,
    email VARCHAR
) AS $$
    DECLARE
        userCount INTEGER;
        userData RECORD;
    BEGIN
        SELECT COUNT(*) INTO userCount
        FROM activationTokens
        WHERE userid = _userid
        AND token = _token;

        IF (userCount = 0) THEN
            RETURN QUERY
            SELECT FALSE, CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR);
        ELSE
            DELETE
            FROM activationTokens
            WHERE userid = _userid;

            IF NOT FOUND THEN
                RETURN QUERY
                SELECT FALSE, CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR);
            ELSE
                UPDATE users
                SET activated = 1
                WHERE id = _userid
                RETURNING users.name, users.surname, users.email INTO userData;
                
                IF NOT FOUND THEN
                    RETURN QUERY
                    SELECT FALSE, CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR), CAST(NULL AS VARCHAR);
                ELSE
                    RETURN QUERY
                    SELECT TRUE, CAST(userData.name AS VARCHAR), CAST(userData.surname AS VARCHAR), CAST(userData.email AS VARCHAR);
                END IF;

            END IF;

        END IF;

    END;
$$ language plpgsql;
