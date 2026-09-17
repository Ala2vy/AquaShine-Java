-- V3: Add wash_services table with seed data
CREATE TABLE wash_services (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(30) NOT NULL UNIQUE,
    name            VARCHAR(60) NOT NULL,
    category        VARCHAR(20) NOT NULL,
    base_price      INTEGER NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_services_category ON wash_services(category);
CREATE INDEX idx_services_active ON wash_services(active);

-- Seed data
INSERT INTO wash_services (code, name, category, base_price, active) VALUES
    ('BASIC',   'Basic Wash',        'BASIC',   500,  TRUE),
    ('PREMIUM', 'Premium Wash',      'PREMIUM', 1000, TRUE),
    ('DELUXE',  'Deluxe Detail',     'DELUXE',  2000, TRUE);