-- Add password_hash column to users table
-- This migration adds password hashing support to the library system

-- Add password_hash column to users table
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NOT NULL DEFAULT '';

-- Update existing users to have a default password hash
-- In production, you would want to force password reset for existing users
UPDATE users SET password_hash = '' WHERE password_hash IS NULL OR password_hash = '';

-- Add index on password_hash for performance (optional)
CREATE INDEX idx_users_password_hash ON users(password_hash);

PRINT 'Password hash column added successfully to users table'; 