CREATE OR REPLACE FUNCTION getFriendsId(userId INTEGER)
RETURNS TABLE (
    id INTEGER
) AS $$
    BEGIN
        RETURN QUERY
            SELECT inviterid
            FROM friends
            WHERE inviteeid = userId
            AND status = true
                UNION (
                        SELECT inviteeid
                        FROM friends
                        WHERE inviterid = userId
                        AND status = true);
    END;
$$ language plpgsql;

CREATE OR REPLACE FUNCTION getActivationTokenIdsToDelete()    
RETURNS TABLE (
    userid INTEGER
) AS $$
    BEGIN
        RETURN QUERY
            SELECT activationTokens.userid
            FROM activationTokens
            WHERE EXTRACT(EPOCH FROM current_timestamp - time_log) / 108000 >= 30;
    END;
$$ language plpgsql;


CREATE OR REPLACE FUNCTION cleanActivationTokens()    
RETURNS TRIGGER AS $$
    BEGIN
        DELETE FROM users
        WHERE id IN (SELECT * FROM getActivationTokenIdsToDelete());
        
        RETURN NEW;
    END;
$$ language plpgsql;
