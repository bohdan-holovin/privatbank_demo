CREATE TABLE accounts
(
    id             BIGSERIAL PRIMARY KEY,

    number         VARCHAR(20) NOT NULL,
    status         VARCHAR(20),
    user_id        BIGINT      NOT NULL,

    created_date   TIMESTAMP   NOT NULL,
    modified_date  TIMESTAMP   NOT NULL,
    created_by_id  BIGINT,
    modified_by_id BIGINT,

    CONSTRAINT uk_accounts_number UNIQUE (number),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_accounts_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_accounts_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);

CREATE INDEX idx_accounts_user_id ON accounts (user_id);
CREATE INDEX idx_accounts_created_date ON accounts (created_date);
CREATE INDEX idx_accounts_status_created_date ON accounts (status, created_date);
