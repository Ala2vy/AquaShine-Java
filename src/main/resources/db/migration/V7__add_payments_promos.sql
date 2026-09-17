-- V7: Payments + Promos
CREATE TABLE promos (
    id                  BIGSERIAL PRIMARY KEY,
    code                VARCHAR(20) NOT NULL UNIQUE,
    discount_percent    INTEGER NOT NULL,
    min_amount          INTEGER NOT NULL,
    max_uses            INTEGER NOT NULL,
    used_count          INTEGER NOT NULL DEFAULT 0,
    expiry_date         TIMESTAMP NOT NULL,
    active              BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE payments (
    id                  BIGSERIAL PRIMARY KEY,
    booking_id          BIGINT NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    user_id             BIGINT NOT NULL REFERENCES users(id),
    original_amount     INTEGER NOT NULL,
    discount_amount     INTEGER NOT NULL DEFAULT 0,
    final_amount        INTEGER NOT NULL,
    promo_code          VARCHAR(20),
    status              VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    transaction_id      VARCHAR(50),
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_booking ON payments(booking_id);

-- Seed promos
INSERT INTO promos (code, discount_percent, min_amount, max_uses, used_count, expiry_date, active) VALUES
    ('WASH50',  50, 1000, 100,  0, '2027-12-31 23:59:59', TRUE),
    ('FIRST10', 10,  500, 1000, 0, '2027-12-31 23:59:59', TRUE),
    ('SAVE20',  20,  800,  200, 0, '2027-12-31 23:59:59', TRUE);