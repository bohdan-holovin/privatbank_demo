CREATE TABLE transactions
(
    id              BIGSERIAL PRIMARY KEY,

    uuid            VARCHAR(36)    NOT NULL,
    from_account_id BIGINT,
    to_account_id   BIGINT,
    amount          DECIMAL(15, 2) NOT NULL,
    processed_at    TIMESTAMP,

    type            VARCHAR(20),
    status          VARCHAR(20),

    failure_reason  VARCHAR(200),

    created_date    TIMESTAMP      NOT NULL,
    modified_date   TIMESTAMP      NOT NULL,
    created_by_id   BIGINT,
    modified_by_id  BIGINT,

    CONSTRAINT uk_transactions_uuid UNIQUE (uuid),
    CONSTRAINT ck_transactions_amount CHECK (amount >= 0),
    CONSTRAINT fk_transactions_from_account FOREIGN KEY (from_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_to_account FOREIGN KEY (to_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_transactions_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);

CREATE INDEX idx_transactions_status_created_date ON transactions (status, created_date);
CREATE INDEX idx_transactions_from_account_created ON transactions (from_account_id, created_date DESC);
CREATE INDEX idx_transactions_to_account_created ON transactions (to_account_id, created_date DESC);
CREATE INDEX idx_transactions_accounts_created ON transactions (from_account_id, to_account_id, created_date DESC);
