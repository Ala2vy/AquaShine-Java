-- V8: Admin features (blocked slots, refund requests, slot capacity)

CREATE TABLE blocked_slots (
    id              BIGSERIAL PRIMARY KEY,
    slot_date       DATE NOT NULL,
    slot_time       TIME NOT NULL,
    reason          VARCHAR(200),
    created_by      BIGINT REFERENCES users(id),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (slot_date, slot_time)
);

CREATE TABLE refund_requests (
    id              BIGSERIAL PRIMARY KEY,
    booking_id      BIGINT NOT NULL REFERENCES bookings(id),
    user_id         BIGINT NOT NULL REFERENCES users(id),
    amount          INTEGER NOT NULL,
    reason          VARCHAR(500),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by     BIGINT REFERENCES users(id),
    reviewed_at     TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refund_status ON refund_requests(status);
CREATE INDEX idx_blocked_slots_date ON blocked_slots(slot_date);