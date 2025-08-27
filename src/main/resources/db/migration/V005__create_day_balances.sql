CREATE TABLE day_balances
(
    id                BIGSERIAL PRIMARY KEY,

    account_id        BIGINT         NOT NULL,
    balance_date      DATE           NOT NULL,
    available_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    pending_balance   DECIMAL(15, 2) NOT NULL DEFAULT 0.00,

    created_date      TIMESTAMP      NOT NULL,
    modified_date     TIMESTAMP      NOT NULL,
    created_by_id     BIGINT,
    modified_by_id    BIGINT,

    CONSTRAINT uk_day_balances_account_date UNIQUE (account_id, balance_date),
    CONSTRAINT fk_day_balances_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_day_balances_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_day_balances_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);
