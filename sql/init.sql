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

CREATE TABLE tables(
    id serial,
    taken BOOLEAN NOT NULL DEFAULT false,
    userid INTEGER,
    PRIMARY KEY (id),
    FOREIGN KEY (userid) REFERENCES users(id)
);

CREATE TABLE admins(
    login VARCHAR(30),
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (login)
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
