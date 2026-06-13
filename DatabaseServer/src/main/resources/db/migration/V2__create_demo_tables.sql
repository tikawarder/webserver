-- Demo tables for the SQL/JPA learning module
-- Entities: Customer, Order, CustomerProfile, Tag, demo_order_tags (ManyToMany join table)

CREATE TABLE demo_customers (
    id    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    city  VARCHAR(255) NOT NULL,
    UNIQUE KEY uk_customer_email (email),
    INDEX idx_customer_email (email),
    INDEX idx_customer_city  (city)
);

CREATE TABLE demo_orders (
    id         BIGINT         AUTO_INCREMENT PRIMARY KEY,
    product    VARCHAR(255)   NOT NULL,
    amount     DECIMAL(38, 2) NOT NULL,
    status     VARCHAR(50)    NOT NULL,
    createdAt  DATETIME(6)    NOT NULL,
    customer_id BIGINT        NOT NULL,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES demo_customers (id),
    INDEX idx_order_customer_id (customer_id),
    INDEX idx_order_status      (status)
);

CREATE TABLE demo_profiles (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    phone       VARCHAR(255),
    bio         VARCHAR(255),
    avatarUrl   VARCHAR(255),
    customer_id BIGINT       NOT NULL,
    UNIQUE KEY uk_profile_customer (customer_id),
    CONSTRAINT fk_profile_customer FOREIGN KEY (customer_id) REFERENCES demo_customers (id)
);

CREATE TABLE demo_tags (
    id   BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    UNIQUE KEY uk_tag_name (name)
);

-- ManyToMany join table: Order <-> Tag
CREATE TABLE demo_order_tags (
    order_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (order_id, tag_id),
    CONSTRAINT fk_ot_order FOREIGN KEY (order_id) REFERENCES demo_orders (id),
    CONSTRAINT fk_ot_tag   FOREIGN KEY (tag_id)   REFERENCES demo_tags (id)
);
