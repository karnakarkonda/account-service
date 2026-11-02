CREATE TABLE  account_number_sequence
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id)
    );

CREATE TABLE  account
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(20) NOT NULL,
    customer_id BINARY(16) NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_account_account_number (account_number)
    );