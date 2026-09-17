-- V1: Initial schema for AquaShine

CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(50) NOT NULL,
    phone           VARCHAR(13) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
