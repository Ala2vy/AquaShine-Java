-- V4: Add admin role to users
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Make abc@gmail.com an admin for demo purposes
UPDATE users SET role = 'ADMIN' WHERE email = 'abc@gmail.com';