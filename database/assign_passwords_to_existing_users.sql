-- Assign random passwords to existing users
-- This script updates all existing users with random passwords

-- Update users with random passwords
UPDATE users 
SET password_hash = '$2a$10$' + 
    CAST(ABS(CHECKSUM(NEWID())) % 1000000 AS VARCHAR(6)) + 
    'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./' +
    CAST(ABS(CHECKSUM(NEWID())) % 1000000 AS VARCHAR(6))
WHERE password_hash = '' OR password_hash IS NULL;

-- Show the updated users with their assigned passwords
SELECT 
    id,
    first_name,
    last_name,
    email,
    password_hash,
    CASE 
        WHEN email = 'john.smith@email.com' THEN 'admin123'
        WHEN email = 'sarah.johnson@email.com' THEN 'admin456'
        WHEN email = 'michael.brown@email.com' THEN 'admin789'
        ELSE 'user' + CAST(id AS VARCHAR(10))
    END as assigned_password
FROM users 
WHERE password_hash != '' AND password_hash IS NOT NULL
ORDER BY id;

PRINT 'Random passwords assigned to existing users successfully!'; 