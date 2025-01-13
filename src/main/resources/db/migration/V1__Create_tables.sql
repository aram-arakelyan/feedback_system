CREATE TABLE customer
(
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(255) UNIQUE                 NOT NULL,
    password_hash    VARCHAR(255)                        NOT NULL,
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE establishment
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)                        NOT NULL,
    address     VARCHAR(255),
    type        VARCHAR(50)                         NOT NULL, -- e.g., 'RESTAURANT', 'SHOP', 'HOTEL'
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE feedback
(
    id               BIGSERIAL PRIMARY KEY,
    customer_id          BIGINT                                 NOT NULL,
    establishment_id BIGINT                                 NOT NULL,
    title            VARCHAR(50)                            NOT NULL,
    text_comment     VARCHAR(1000),
    score            INT CHECK (score >= 0 AND score <= 10) NOT NULL,
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP    NOT NULL,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE,
    CONSTRAINT fk_establishment FOREIGN KEY (establishment_id) REFERENCES establishment (id) ON DELETE CASCADE,
    CONSTRAINT customer_establishment_unique UNIQUE (customer_id, establishment_id) -- One feedback per customer per establishment
);