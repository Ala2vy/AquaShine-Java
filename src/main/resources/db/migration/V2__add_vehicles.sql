-- V2: Add vehicles table
CREATE TABLE vehicles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plate_number    VARCHAR(15) NOT NULL,
    type            VARCHAR(20) NOT NULL,
    make            VARCHAR(30),
    model           VARCHAR(30),
    year_made       INTEGER NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vehicles_user ON vehicles(user_id);
CREATE UNIQUE INDEX idx_vehicles_plate ON vehicles(user_id, plate_number);