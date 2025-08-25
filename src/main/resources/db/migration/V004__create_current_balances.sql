CREATE TABLE current_balances
(
    id                  BIGSERIAL PRIMARY KEY,

    available_balance   DECIMAL(15, 2) NOT NULL,
    pending_balance     DECIMAL(15, 2) DEFAULT 0.00,
    account_id          BIGINT,
    last_transaction_id BIGINT,

    created_date        TIMESTAMP      NOT NULL,
    modified_date       TIMESTAMP      NOT NULL,
    created_by_id       BIGINT,
    modified_by_id      BIGINT,

    CONSTRAINT fk_current_balances_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT fk_current_balances_last_transaction FOREIGN KEY (last_transaction_id) REFERENCES transactions (id),
    CONSTRAINT fk_current_balances_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_current_balances_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);