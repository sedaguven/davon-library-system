-- Add role column to users table
ALTER TABLE users ADD role VARCHAR(50) NOT NULL DEFAULT 'user';
GO

-- Update existing users to have the default 'user' role
UPDATE users SET role = 'user' WHERE role IS NULL;

-- Optionally, assign 'admin' role to specific users
-- UPDATE users SET role = 'admin' WHERE email IN ('john.smith@email.com', 'seda.guven@example.com'); 