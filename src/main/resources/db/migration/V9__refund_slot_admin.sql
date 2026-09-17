-- V9: Extend refunds with review metadata + create missing tables
-- (tables were already added in V8, this ensures they're set up correctly)

-- Ensure the refund_requests table has correct defaults
ALTER TABLE refund_requests ALTER COLUMN status SET DEFAULT 'PENDING';

-- Ensure blocked_slots has unique constraint
ALTER TABLE blocked_slots DROP CONSTRAINT IF EXISTS blocked_slots_slot_date_slot_time_key;
ALTER TABLE blocked_slots ADD CONSTRAINT blocked_slots_slot_date_slot_time_key UNIQUE (slot_date, slot_time);