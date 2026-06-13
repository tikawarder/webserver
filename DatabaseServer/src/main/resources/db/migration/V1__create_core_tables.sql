-- Core application tables: users (Person entity) and accounts (Account entity)

CREATE TABLE users (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    birthDay DATE         NOT NULL,
    city     VARCHAR(255) NOT NULL
);

CREATE TABLE accounts (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(50)  NOT NULL
);
