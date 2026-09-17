-- V10: Allow PAID status on bookings (set by payment service)
-- No structural change needed since status is VARCHAR(20)
-- This migration is a no-op marker for the schema version bump
SELECT 1;