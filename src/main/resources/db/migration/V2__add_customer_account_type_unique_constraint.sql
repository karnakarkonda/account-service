ALTER TABLE account
    ADD CONSTRAINT uk_customer_account_type
        UNIQUE (customer_id, account_type);