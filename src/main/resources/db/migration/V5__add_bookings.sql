-- V5: Bookings table
CREATE TABLE bookings (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vehicle_id      BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    service_id      BIGINT NOT NULL REFERENCES wash_services(id),
    slot_date       DATE NOT NULL,
    slot_time       TIME NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    total_price     INTEGER NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_slot ON bookings(slot_date, slot_time);