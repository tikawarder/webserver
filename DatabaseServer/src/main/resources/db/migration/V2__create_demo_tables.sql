-- Demo tables for the SQL/JPA learning module
-- Entities: Customer, Order, CustomerProfile, Tag, demo_order_tags (ManyToMany join table)

CREATE TABLE demo_customers (
    id    BIGSERIAL    PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    city  VARCHAR(255) NOT NULL
);

CREATE INDEX idx_customer_email ON demo_customers (email);
CREATE INDEX idx_customer_city  ON demo_customers (city);

CREATE TABLE demo_orders (
    id          BIGSERIAL      PRIMARY KEY,
    product     VARCHAR(255)   NOT NULL,
    amount      DECIMAL(38, 2) NOT NULL,
    status      VARCHAR(50)    NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    customer_id BIGINT         NOT NULL REFERENCES demo_customers (id)
);

CREATE INDEX idx_order_customer_id ON demo_orders (customer_id);
CREATE INDEX idx_order_status      ON demo_orders (status);

CREATE TABLE demo_profiles (
    id          BIGSERIAL    PRIMARY KEY,
    phone       VARCHAR(255),
    bio         VARCHAR(255),
    avatar_url  VARCHAR(255),
    customer_id BIGINT       NOT NULL UNIQUE REFERENCES demo_customers (id)
);

CREATE TABLE demo_tags (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- ManyToMany join table: Order <-> Tag
CREATE TABLE demo_order_tags (
    order_id BIGINT NOT NULL REFERENCES demo_orders (id),
    tag_id   BIGINT NOT NULL REFERENCES demo_tags (id),
    PRIMARY KEY (order_id, tag_id)
);
