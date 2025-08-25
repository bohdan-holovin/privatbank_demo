CREATE TABLE users
(
    id             BIGSERIAL PRIMARY KEY,

    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(255) UNIQUE,
    phone          VARCHAR(20),

    created_date   TIMESTAMP    NOT NULL,
    modified_date  TIMESTAMP    NOT NULL,
    created_by_id  BIGINT,
    modified_by_id BIGINT,

    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by_id) REFERENCES users (id),
    CONSTRAINT fk_users_modified_by FOREIGN KEY (modified_by_id) REFERENCES users (id)
);
