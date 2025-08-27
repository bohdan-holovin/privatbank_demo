CREATE TABLE users
(
    id             BIGSERIAL PRIMARY KEY,

    username       VARCHAR(100) NOT NULL,

    created_date   TIMESTAMP    NOT NULL,
    modified_date  TIMESTAMP    NOT NULL,
    created_by_id  BIGINT,
    modified_by_id BIGINT,

    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_users_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);
