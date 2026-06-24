CREATE TABLE IF NOT EXISTS persons (
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    birth_day DATE        NOT NULL,
    city     VARCHAR(100) NOT NULL
);
