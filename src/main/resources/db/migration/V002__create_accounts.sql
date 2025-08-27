CREATE TABLE accounts
(
    id             BIGSERIAL PRIMARY KEY,

    number         VARCHAR(20) NOT NULL UNIQUE,
    status         VARCHAR(20),
    user_id        BIGINT      NOT NULL,

    created_date   TIMESTAMP   NOT NULL,
    modified_date  TIMESTAMP   NOT NULL,
    created_by_id  BIGINT,
    modified_by_id BIGINT,

    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_accounts_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_accounts_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);
