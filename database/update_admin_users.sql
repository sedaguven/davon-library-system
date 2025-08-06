-- Update specific users to have the 'admin' role
UPDATE users
SET role = 'admin'
WHERE email IN (
  'john.smith@email.com',
  'sarah.johnson@email.com',
  'michael.brown@email.com'
); 