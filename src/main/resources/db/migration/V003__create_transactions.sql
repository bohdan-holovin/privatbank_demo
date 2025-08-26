CREATE TABLE transactions
(
    id              BIGSERIAL PRIMARY KEY,

    uuid            VARCHAR(36)    NOT NULL UNIQUE,
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

    CONSTRAINT fk_transactions_from_account FOREIGN KEY (from_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_to_account FOREIGN KEY (to_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_transactions_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);
