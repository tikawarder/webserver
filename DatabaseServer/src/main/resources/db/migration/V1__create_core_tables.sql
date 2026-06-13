-- Core application tables: users (Person entity) and accounts (Account entity)

CREATE TABLE users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    birthDay DATE         NOT NULL,
    city     VARCHAR(255) NOT NULL
);

CREATE TABLE accounts (
    id       BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(50)  NOT NULL,
    UNIQUE KEY uk_accounts_username (username)
);
