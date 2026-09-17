-- V6: Add cancellation support to bookings
ALTER TABLE bookings ADD COLUMN cancelled_at TIMESTAMP;
ALTER TABLE bookings ADD COLUMN cancelled_reason VARCHAR(200);

-- Add index for status queries
CREATE INDEX idx_bookings_status ON bookings(status);