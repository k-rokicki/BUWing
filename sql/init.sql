CREATE TABLE users(
    id serial,
    login VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    activated INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE friends(
    inviterid INTEGER NOT NULL,
    inviteeid INTEGER NOT NULL,
    status BOOLEAN NOT NULL DEAFAULT false,
    PRIMARY KEY (inviterid, inviteeid),
    FOREIGN KEY (inviterid) REFERENCES users(id),
    FOREIGN KEY (inviteeid) REFERENCES users(id)
);

CREATE TABLE tables(
    id serial,
    floor INTEGER NOT NULL,
    taken BOOLEAN NOT NULL DEFAULT false,
    userid INTEGER DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (userid) REFERENCES users(id),
    CHECK(floor BETWEEN 1 AND 3)
);

CREATE TABLE activationTokens(
    userid INTEGER NOT NULL,
    token VARCHAR(50) NOT NULL,
    PRIMARY KEY (userid),
    FOREIGN KEY (userid) REFERENCES users(id)
);

CREATE TABLE pendingPasswordChanges(
    userid INTEGER NOT NULL,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(50) NOT NULL,
    PRIMARY KEY (userid),
    FOREIGN KEY (userid) REFERENCES users(id)
);

CREATE TABLE resetPasswordTokens(
    userid INTEGER NOT NULL,
    token VARCHAR(50) NOT NULL,
    PRIMARY KEY (userid),
    FOREIGN KEY (userid) REFERENCES users(id)
);

CREATE TABLE pendingAccountDeletions(
    userid INTEGER NOT NULL,
    token VARCHAR(50) NOT NULL,
    PRIMARY KEY (userid),
    FOREIGN KEY (userid) REFERENCES users(id)
);

CREATE OR REPLACE FUNCTION getFriendsId(userId INTEGER)
RETURNS TABLE (
    id INTEGER
) AS $$
    BEGIN
        RETURN QUERY
            SELECT inviterid
            FROM friends
            WHERE inviteeid = userId AND status = 't'
                UNION (
                        SELECT inviteeid
                        FROM friends
                        WHERE inviterid = userId AND status = 't');
    END;
$$ language plpgsql;
