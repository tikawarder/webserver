-- PostgreSQL init script
-- Note: Database is created by docker-compose POSTGRES_DB variable

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birthDay DATE NOT NULL,
    city VARCHAR(100) NOT NULL
);

CREATE DATABASE authdb;