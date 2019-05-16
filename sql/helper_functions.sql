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
