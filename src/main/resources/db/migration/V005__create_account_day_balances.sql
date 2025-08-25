CREATE TABLE account_day_balances
(
    id                BIGSERIAL PRIMARY KEY,

    account_id        BIGINT         NOT NULL,
    balance_date      DATE           NOT NULL,
    opening_balance   DECIMAL(15, 2) NOT NULL,
    closing_balance   DECIMAL(15, 2) NOT NULL,
    total_debits      DECIMAL(15, 2) DEFAULT 0.00,
    total_credits     DECIMAL(15, 2) DEFAULT 0.00,
    transaction_count INTEGER        DEFAULT 0,

    created_date      TIMESTAMP      NOT NULL,
    modified_date     TIMESTAMP      NOT NULL,
    created_by_id     BIGINT,
    modified_by_id    BIGINT,

    CONSTRAINT fk_account_day_balances_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_account_day_balances_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_account_day_balances_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);