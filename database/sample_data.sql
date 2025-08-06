-- =====================================================
-- Library Management System - Sample Data
-- =====================================================

-- =====================================================
-- INSERT ENUMERATION DATA
-- =====================================================

-- Book Copy Status
INSERT INTO book_copy_status (status_name, description) VALUES
('AVAILABLE', 'Book copy is available for borrowing'),
('CHECKED_OUT', 'Book copy is currently checked out'),
('RESERVED', 'Book copy is reserved for a user'),
('MAINTENANCE', 'Book copy is under maintenance'),
('LOST', 'Book copy has been reported lost'),
('DAMAGED', 'Book copy is damaged and unavailable');

-- Loan Status
INSERT INTO loan_status (status_name, description) VALUES
('ACTIVE', 'Loan is currently active'),
('RETURNED', 'Book has been returned'),
('OVERDUE', 'Loan is overdue'),
('LOST', 'Book has been reported lost'),
('DAMAGED', 'Book has been reported damaged');

-- Reservation Status
INSERT INTO reservation_status (status_name, description) VALUES
('ACTIVE', 'Reservation is active'),
('FULFILLED', 'Reservation has been fulfilled'),
('CANCELLED', 'Reservation has been cancelled'),
('EXPIRED', 'Reservation has expired');

-- Membership Status
INSERT INTO membership_status (status_name, description) VALUES
('ACTIVE', 'Membership is active'),
('SUSPENDED', 'Membership is suspended'),
('EXPIRED', 'Membership has expired'),
('CANCELLED', 'Membership has been cancelled');

-- Employment Status
INSERT INTO employment_status (status_name, description) VALUES
('ACTIVE', 'Employee is actively employed'),
('ON_LEAVE', 'Employee is on leave'),
('SUSPENDED', 'Employee is suspended'),
('TERMINATED', 'Employee has been terminated'),
('RETIRED', 'Employee has retired');

-- Fine Status
INSERT INTO fine_status (status_name, description) VALUES
('ACTIVE', 'Fine is active and unpaid'),
('PAID', 'Fine has been paid in full'),
('WAIVED', 'Fine has been waived'),
('CANCELLED', 'Fine has been cancelled'),
('PARTIALLY_PAID', 'Fine has been partially paid');

-- Notification Type
INSERT INTO notification_type (type_name, description) VALUES
('OVERDUE_REMINDER', 'Reminder for overdue books'),
('DUE_SOON_REMINDER', 'Reminder for books due soon'),
('RESERVATION_AVAILABLE', 'Notification that reserved book is available'),
('RESERVATION_EXPIRING', 'Reminder that reservation is expiring'),
('FINE_ACCUMULATED', 'Notification of accumulated fines'),
('FINE_PAID', 'Confirmation of fine payment'),
('FINE_WAIVED', 'Notification of fine waiver'),
('BOOK_RETURNED', 'Confirmation of book return'),
('LOAN_EXTENDED', 'Confirmation of loan extension'),
('SYSTEM_MAINTENANCE', 'System maintenance notification'),
('WELCOME_MESSAGE', 'Welcome message for new members'),
('ACCOUNT_SUSPENDED', 'Notification of account suspension');

-- Notification Priority
INSERT INTO notification_priority (priority_name, description) VALUES
('LOW', 'Low priority notification'),
('NORMAL', 'Normal priority notification'),
('HIGH', 'High priority notification'),
('URGENT', 'Urgent notification');

-- Report Type
INSERT INTO report_type (type_name, description) VALUES
('OVERDUE_BOOKS', 'Report of overdue books'),
('FINE_COLLECTION', 'Report of fine collection'),
('POPULAR_BOOKS', 'Report of popular books'),
('USER_ACTIVITY', 'Report of user activity'),
('LOAN_STATISTICS', 'Report of loan statistics'),
('RESERVATION_QUEUE', 'Report of reservation queues'),
('INVENTORY_STATUS', 'Report of inventory status'),
('FINANCIAL_SUMMARY', 'Report of financial summary'),
('STAFF_PERFORMANCE', 'Report of staff performance'),
('SYSTEM_USAGE', 'Report of system usage');

-- Report Status
INSERT INTO report_status (status_name, description) VALUES
('PENDING', 'Report is pending generation'),
('PROCESSING', 'Report is being processed'),
('COMPLETED', 'Report has been completed'),
('FAILED', 'Report generation failed'),
('CANCELLED', 'Report generation was cancelled');

-- =====================================================
-- INSERT CORE ENTITY DATA
-- =====================================================

-- Authors
INSERT INTO authors (first_name, last_name) VALUES
('J.K.', 'Rowling'),
('George R.R.', 'Martin'),
('Stephen', 'King'),
('Agatha', 'Christie'),
('Ernest', 'Hemingway'),
('Jane', 'Austen'),
('Mark', 'Twain'),
('Charles', 'Dickens'),
('William', 'Shakespeare'),
('F. Scott', 'Fitzgerald'),
('Harper', 'Lee'),
('J.R.R.', 'Tolkien'),
('Virginia', 'Woolf'),
('Gabriel', 'García Márquez'),
('Toni', 'Morrison'),
('Kazuo', 'Ishiguro'),
('Margaret', 'Atwood'),
('Salman', 'Rushdie'),
('Chimamanda Ngozi', 'Adichie'),
('Colson', 'Whitehead');

-- Libraries
INSERT INTO libraries (name, address, city) VALUES
('Central Library', '123 Main Street', 'New York'),
('Downtown Branch', '456 Oak Avenue', 'New York'),
('Westside Library', '789 Pine Road', 'Los Angeles'),
('Eastside Branch', '321 Elm Street', 'Los Angeles'),
('North Library', '654 Maple Drive', 'Chicago'),
('South Branch', '987 Cedar Lane', 'Chicago'),
('University Library', '147 College Boulevard', 'Boston'),
('Community Library', '258 Park Street', 'Boston'),
('Metropolitan Library', '369 River Road', 'San Francisco'),
('Harbor Branch', '741 Bay Avenue', 'San Francisco');

-- Books
INSERT INTO books (title, isbn, available_copies, total_copies, author_id) VALUES
('Harry Potter and the Philosopher''s Stone', '978-0-7475-3269-9', 3, 5, 1),
('Harry Potter and the Chamber of Secrets', '978-0-7475-3849-3', 2, 4, 1),
('Harry Potter and the Prisoner of Azkaban', '978-0-7475-4215-5', 1, 3, 1),
('A Game of Thrones', '978-0-553-10354-0', 2, 3, 2),
('A Clash of Kings', '978-0-553-10803-3', 1, 2, 2),
('The Shining', '978-0-385-12167-5', 2, 4, 3),
('It', '978-0-670-81302-5', 1, 3, 3),
('Murder on the Orient Express', '978-0-06-207350-1', 3, 5, 4),
('Death on the Nile', '978-0-06-207356-3', 2, 3, 4),
('The Old Man and the Sea', '978-0-684-80122-3', 1, 2, 5),
('For Whom the Bell Tolls', '978-0-684-80335-7', 2, 3, 5),
('Pride and Prejudice', '978-0-14-143951-8', 4, 6, 6),
('Emma', '978-0-14-143958-7', 2, 4, 6),
('The Adventures of Tom Sawyer', '978-0-14-303956-3', 1, 2, 7),
('Adventures of Huckleberry Finn', '978-0-14-243717-9', 2, 3, 7),
('Great Expectations', '978-0-14-143956-3', 3, 5, 8),
('A Tale of Two Cities', '978-0-14-143960-0', 1, 3, 8),
('Romeo and Juliet', '978-0-7434-7752-4', 2, 4, 9),
('Hamlet', '978-0-7434-7753-1', 1, 2, 9),
('The Great Gatsby', '978-0-7432-7356-5', 3, 4, 10),
('To Kill a Mockingbird', '978-0-06-112008-4', 2, 3, 11),
('The Hobbit', '978-0-618-00221-4', 4, 6, 12),
('The Lord of the Rings', '978-0-618-00222-1', 2, 4, 12),
('Mrs. Dalloway', '978-0-15-662870-9', 1, 2, 13),
('One Hundred Years of Solitude', '978-0-06-088328-7', 2, 3, 14),
('Beloved', '978-1-4000-3341-4', 1, 2, 15),
('The Remains of the Day', '978-0-679-73172-9', 2, 3, 16),
('The Handmaid''s Tale', '978-0-345-54001-4', 3, 4, 17),
('Midnight''s Children', '978-0-8129-7653-0', 1, 2, 18),
('Americanah', '978-0-307-96212-6', 2, 3, 19),
('The Underground Railroad', '978-0-385-54236-4', 1, 2, 20);

-- Users
INSERT INTO users (first_name, last_name, email) VALUES
('John', 'Smith', 'john.smith@email.com'),
('Sarah', 'Johnson', 'sarah.johnson@email.com'),
('Michael', 'Brown', 'michael.brown@email.com'),
('Emily', 'Davis', 'emily.davis@email.com'),
('David', 'Wilson', 'david.wilson@email.com'),
('Lisa', 'Anderson', 'lisa.anderson@email.com'),
('Robert', 'Taylor', 'robert.taylor@email.com'),
('Jennifer', 'Martinez', 'jennifer.martinez@email.com'),
('Christopher', 'Garcia', 'christopher.garcia@email.com'),
('Amanda', 'Rodriguez', 'amanda.rodriguez@email.com'),
('James', 'Miller', 'james.miller@email.com'),
('Jessica', 'Moore', 'jessica.moore@email.com'),
('Daniel', 'Jackson', 'daniel.jackson@email.com'),
('Ashley', 'Martin', 'ashley.martin@email.com'),
('Matthew', 'Lee', 'matthew.lee@email.com'),
('Nicole', 'Perez', 'nicole.perez@email.com'),
('Joshua', 'Thompson', 'joshua.thompson@email.com'),
('Stephanie', 'White', 'stephanie.white@email.com'),
('Andrew', 'Harris', 'andrew.harris@email.com'),
('Rachel', 'Clark', 'rachel.clark@email.com'),
('Kevin', 'Lewis', 'kevin.lewis@email.com'),
('Lauren', 'Robinson', 'lauren.robinson@email.com'),
('Brian', 'Walker', 'brian.walker@email.com'),
('Michelle', 'Young', 'michelle.young@email.com'),
('Steven', 'Allen', 'steven.allen@email.com'),
('Kimberly', 'King', 'kimberly.king@email.com'),
('Timothy', 'Wright', 'timothy.wright@email.com'),
('Heather', 'Lopez', 'heather.lopez@email.com'),
('Jeffrey', 'Hill', 'jeffrey.hill@email.com'),
('Melissa', 'Scott', 'melissa.scott@email.com'),
('Ryan', 'Green', 'ryan.green@email.com'),
('Tiffany', 'Adams', 'tiffany.adams@email.com'),
('Gary', 'Baker', 'gary.baker@email.com'),
('Christina', 'Gonzalez', 'christina.gonzalez@email.com'),
('Eric', 'Nelson', 'eric.nelson@email.com'),
('Rebecca', 'Carter', 'rebecca.carter@email.com'),
('Stephen', 'Mitchell', 'stephen.mitchell@email.com'),
('Laura', 'Perez', 'laura.perez@email.com'),
('Gregory', 'Roberts', 'gregory.roberts@email.com'),
('Megan', 'Turner', 'megan.turner@email.com');

-- Staff
INSERT INTO staff (first_name, last_name, email, position, department, employee_id, hire_date, employment_status_id, library_id) VALUES
('Patricia', 'Librarian', 'patricia.librarian@library.com', 'Head Librarian', 'Administration', 'EMP001', '2020-01-15', 1, 1),
('Thomas', 'Manager', 'thomas.manager@library.com', 'Library Manager', 'Management', 'EMP002', '2019-03-20', 1, 1),
('Nancy', 'Assistant', 'nancy.assistant@library.com', 'Library Assistant', 'Circulation', 'EMP003', '2021-06-10', 1, 1),
('Karen', 'Clerk', 'karen.clerk@library.com', 'Circulation Clerk', 'Circulation', 'EMP004', '2022-02-28', 1, 2),
('Betty', 'Specialist', 'betty.specialist@library.com', 'Reference Specialist', 'Reference', 'EMP005', '2020-09-15', 1, 2),
('Helen', 'Coordinator', 'helen.coordinator@library.com', 'Program Coordinator', 'Programs', 'EMP006', '2021-11-05', 1, 3),
('Sandra', 'Technician', 'sandra.technician@library.com', 'IT Technician', 'Technology', 'EMP007', '2022-04-12', 1, 3),
('Donna', 'Supervisor', 'donna.supervisor@library.com', 'Circulation Supervisor', 'Circulation', 'EMP008', '2018-07-22', 1, 4),
('Carol', 'Librarian', 'carol.librarian@library.com', 'Children''s Librarian', 'Children''s Services', 'EMP009', '2020-12-03', 1, 4),
('Ruth', 'Assistant', 'ruth.assistant@library.com', 'Technical Assistant', 'Technical Services', 'EMP010', '2021-08-18', 1, 5);

-- Update staff supervisor relationships
UPDATE staff SET supervisor_id = 1 WHERE id = 2;
UPDATE staff SET supervisor_id = 1 WHERE id = 3;
UPDATE staff SET supervisor_id = 2 WHERE id = 4;
UPDATE staff SET supervisor_id = 2 WHERE id = 5;
UPDATE staff SET supervisor_id = 6 WHERE id = 7;
UPDATE staff SET supervisor_id = 8 WHERE id = 9;
UPDATE staff SET supervisor_id = 8 WHERE id = 10;

-- =====================================================
-- INSERT RELATIONSHIP DATA
-- =====================================================

-- Library Memberships
INSERT INTO library_memberships (user_id, library_id, join_date, status_id) VALUES
(1, 1, '2023-01-15', 1),
(2, 1, '2023-02-20', 1),
(3, 2, '2023-03-10', 1),
(4, 2, '2023-04-05', 1),
(5, 3, '2023-05-12', 1),
(6, 3, '2023-06-18', 1),
(7, 4, '2023-07-25', 1),
(8, 4, '2023-08-30', 1),
(9, 5, '2023-09-14', 1),
(10, 5, '2023-10-22', 1),
(11, 1, '2023-11-08', 1),
(12, 2, '2023-12-15', 1),
(13, 3, '2024-01-20', 1),
(14, 4, '2024-02-28', 1),
(15, 5, '2024-03-10', 1),
(16, 1, '2024-04-05', 1),
(17, 2, '2024-05-12', 1),
(18, 3, '2024-06-18', 1),
(19, 4, '2024-07-25', 1),
(20, 5, '2024-08-30', 1),
(21, 1, '2024-09-14', 1),
(22, 2, '2024-10-22', 1),
(23, 3, '2024-11-08', 1),
(24, 4, '2024-12-15', 1),
(25, 5, '2025-01-20', 1),
(26, 1, '2025-02-28', 1),
(27, 2, '2025-03-10', 1),
(28, 3, '2025-04-05', 1),
(29, 4, '2025-05-12', 1),
(30, 5, '2025-06-18', 1);

-- Book Copies
INSERT INTO book_copies (barcode, book_id, library_id, status_id, location) VALUES
-- Central Library copies
('BC001', 1, 1, 1, 'Fiction - Row A, Shelf 1'),
('BC002', 1, 1, 1, 'Fiction - Row A, Shelf 1'),
('BC003', 1, 1, 2, 'Fiction - Row A, Shelf 1'),
('BC004', 1, 1, 2, 'Fiction - Row A, Shelf 1'),
('BC005', 1, 1, 1, 'Fiction - Row A, Shelf 1'),
('BC006', 2, 1, 1, 'Fiction - Row A, Shelf 2'),
('BC007', 2, 1, 1, 'Fiction - Row A, Shelf 2'),
('BC008', 2, 1, 2, 'Fiction - Row A, Shelf 2'),
('BC009', 2, 1, 2, 'Fiction - Row A, Shelf 2'),
('BC010', 3, 1, 1, 'Fiction - Row A, Shelf 3'),
('BC011', 3, 1, 2, 'Fiction - Row A, Shelf 3'),
('BC012', 3, 1, 2, 'Fiction - Row A, Shelf 3'),

-- Downtown Branch copies
('BC013', 4, 2, 1, 'Fantasy - Row B, Shelf 1'),
('BC014', 4, 2, 1, 'Fantasy - Row B, Shelf 1'),
('BC015', 4, 2, 2, 'Fantasy - Row B, Shelf 1'),
('BC016', 5, 2, 1, 'Fantasy - Row B, Shelf 2'),
('BC017', 5, 2, 2, 'Fantasy - Row B, Shelf 2'),
('BC018', 6, 2, 1, 'Horror - Row C, Shelf 1'),
('BC019', 6, 2, 1, 'Horror - Row C, Shelf 1'),
('BC020', 6, 2, 2, 'Horror - Row C, Shelf 1'),
('BC021', 6, 2, 2, 'Horror - Row C, Shelf 1'),

-- Westside Library copies
('BC022', 7, 3, 1, 'Horror - Row C, Shelf 2'),
('BC023', 7, 3, 2, 'Horror - Row C, Shelf 2'),
('BC024', 7, 3, 2, 'Horror - Row C, Shelf 2'),
('BC025', 8, 3, 1, 'Mystery - Row D, Shelf 1'),
('BC026', 8, 3, 1, 'Mystery - Row D, Shelf 1'),
('BC027', 8, 3, 1, 'Mystery - Row D, Shelf 1'),
('BC028', 8, 3, 2, 'Mystery - Row D, Shelf 1'),
('BC029', 8, 3, 2, 'Mystery - Row D, Shelf 1'),

-- Eastside Branch copies
('BC030', 9, 4, 1, 'Mystery - Row D, Shelf 2'),
('BC031', 9, 4, 1, 'Mystery - Row D, Shelf 2'),
('BC032', 9, 4, 2, 'Mystery - Row D, Shelf 2'),
('BC033', 10, 4, 1, 'Classics - Row E, Shelf 1'),
('BC034', 10, 4, 2, 'Classics - Row E, Shelf 1'),
('BC035', 11, 4, 1, 'Classics - Row E, Shelf 2'),
('BC036', 11, 4, 1, 'Classics - Row E, Shelf 2'),
('BC037', 11, 4, 2, 'Classics - Row E, Shelf 2'),

-- North Library copies
('BC038', 12, 5, 1, 'Classics - Row E, Shelf 3'),
('BC039', 12, 5, 1, 'Classics - Row E, Shelf 3'),
('BC040', 12, 5, 1, 'Classics - Row E, Shelf 3'),
('BC041', 12, 5, 2, 'Classics - Row E, Shelf 3'),
('BC042', 12, 5, 2, 'Classics - Row E, Shelf 3'),
('BC043', 12, 5, 2, 'Classics - Row E, Shelf 3'),
('BC044', 13, 5, 1, 'Classics - Row E, Shelf 4'),
('BC045', 13, 5, 1, 'Classics - Row E, Shelf 4'),
('BC046', 13, 5, 2, 'Classics - Row E, Shelf 4'),
('BC047', 13, 5, 2, 'Classics - Row E, Shelf 4');

-- =====================================================
-- INSERT TRANSACTION DATA
-- =====================================================

-- Loans (some active, some returned, some overdue)
INSERT INTO loans (user_id, book_copy_id, loan_date, due_date, return_date, extensions_count, status_id) VALUES
-- Active loans
(1, 3, '2024-12-01 10:30:00', '2024-12-15', NULL, 0, 1),
(2, 4, '2024-12-02 14:15:00', '2024-12-16', NULL, 0, 1),
(3, 8, '2024-12-03 09:45:00', '2024-12-17', NULL, 0, 1),
(4, 9, '2024-12-04 16:20:00', '2024-12-18', NULL, 0, 1),
(5, 11, '2024-12-05 11:10:00', '2024-12-19', NULL, 0, 1),
(6, 12, '2024-12-06 13:25:00', '2024-12-20', NULL, 0, 1),
(7, 15, '2024-12-07 15:40:00', '2024-12-21', NULL, 0, 1),
(8, 17, '2024-12-08 12:05:00', '2024-12-22', NULL, 0, 1),
(9, 20, '2024-12-09 10:50:00', '2024-12-23', NULL, 0, 1),
(10, 21, '2024-12-10 14:30:00', '2024-12-24', NULL, 0, 1),

-- Returned loans
(11, 23, '2024-11-15 09:20:00', '2024-11-29', '2024-11-28 16:45:00', 0, 2),
(12, 24, '2024-11-16 11:35:00', '2024-11-30', '2024-11-29 14:20:00', 0, 2),
(13, 28, '2024-11-17 13:50:00', '2024-12-01', '2024-11-30 17:10:00', 0, 2),
(14, 29, '2024-11-18 15:15:00', '2024-12-02', '2024-12-01 10:30:00', 0, 2),
(15, 32, '2024-11-19 16:40:00', '2024-12-03', '2024-12-02 11:45:00', 0, 2),
(16, 34, '2024-11-20 12:25:00', '2024-12-04', '2024-12-03 13:20:00', 0, 2),
(17, 37, '2024-11-21 14:50:00', '2024-12-05', '2024-12-04 15:35:00', 0, 2),
(18, 41, '2024-11-22 10:15:00', '2024-12-06', '2024-12-05 16:50:00', 0, 2),
(19, 42, '2024-11-23 11:40:00', '2024-12-07', '2024-12-06 12:25:00', 0, 2),
(20, 43, '2024-11-24 13:05:00', '2024-12-08', '2024-12-07 14:40:00', 0, 2),

-- Overdue loans
(21, 46, '2024-11-10 09:30:00', '2024-11-24', NULL, 0, 3),
(22, 47, '2024-11-11 11:45:00', '2024-11-25', NULL, 0, 3),
(23, 44, '2024-11-12 13:20:00', '2024-11-26', NULL, 0, 3),
(24, 45, '2024-11-13 15:35:00', '2024-11-27', NULL, 0, 3),
(25, 36, '2024-11-14 16:50:00', '2024-11-28', NULL, 0, 3);

-- Reservations
INSERT INTO reservations (user_id, book_id, reservation_date, expiry_date, status_id, queue_position) VALUES
(26, 1, '2024-12-01 10:00:00', '2024-12-08', 1, 1),
(27, 4, '2024-12-02 11:00:00', '2024-12-09', 1, 1),
(28, 6, '2024-12-03 12:00:00', '2024-12-10', 1, 1),
(29, 8, '2024-12-04 13:00:00', '2024-12-11', 1, 2),
(30, 10, '2024-12-05 14:00:00', '2024-12-12', 1, 1);

-- Reservation Queues
INSERT INTO reservation_queues (book_id, library_id, queue_length, estimated_wait_days, average_loan_duration, is_active, max_queue_size, notification_threshold) VALUES
(1, 1, 1, 14, 14, 1, 50, 3),
(4, 2, 1, 14, 14, 1, 50, 3),
(6, 2, 1, 14, 14, 1, 50, 3),
(8, 3, 2, 28, 14, 1, 50, 3),
(10, 4, 1, 14, 14, 1, 50, 3);

-- =====================================================
-- INSERT FINANCIAL DATA
-- =====================================================

-- Fines (for overdue loans)
INSERT INTO fines (loan_id, user_id, amount, daily_rate, days_overdue, is_paid, paid_amount, status_id) VALUES
(21, 21, 5.50, 0.50, 11, 0, 0.00, 1),
(22, 22, 6.00, 0.50, 12, 0, 0.00, 1),
(23, 23, 5.00, 0.50, 10, 0, 0.00, 1),
(24, 24, 5.50, 0.50, 11, 0, 0.00, 1),
(25, 25, 6.00, 0.50, 12, 0, 0.00, 1);

-- =====================================================
-- INSERT NOTIFICATION DATA
-- =====================================================

-- Notifications
INSERT INTO notifications (user_id, type_id, title, message, is_read, is_sent, priority_id) VALUES
(21, 1, 'Overdue Book Reminder', 'Your book "Emma" is overdue. Please return it as soon as possible.', 0, 1, 3),
(22, 1, 'Overdue Book Reminder', 'Your book "Adventures of Huckleberry Finn" is overdue. Please return it as soon as possible.', 0, 1, 3),
(23, 1, 'Overdue Book Reminder', 'Your book "Great Expectations" is overdue. Please return it as soon as possible.', 0, 1, 3),
(24, 1, 'Overdue Book Reminder', 'Your book "A Tale of Two Cities" is overdue. Please return it as soon as possible.', 0, 1, 3),
(25, 1, 'Overdue Book Reminder', 'Your book "The Old Man and the Sea" is overdue. Please return it as soon as possible.', 0, 1, 3),
(1, 2, 'Book Due Soon', 'Your book "Harry Potter and the Philosopher''s Stone" is due in 2 days.', 0, 1, 2),
(2, 2, 'Book Due Soon', 'Your book "Harry Potter and the Chamber of Secrets" is due in 2 days.', 0, 1, 2),
(3, 2, 'Book Due Soon', 'Your book "Harry Potter and the Prisoner of Azkaban" is due in 2 days.', 0, 1, 2),
(26, 3, 'Reserved Book Available', 'Your reserved book "Harry Potter and the Philosopher''s Stone" is now available for pickup.', 0, 1, 2),
(27, 3, 'Reserved Book Available', 'Your reserved book "A Game of Thrones" is now available for pickup.', 0, 1, 2);

-- =====================================================
-- INSERT REPORT DATA
-- =====================================================

-- Reports
INSERT INTO reports (type_id, title, description, generated_date, status_id, total_records, total_amount) VALUES
(1, 'Overdue Books Report - December 2024', 'Report of all overdue books as of December 2024', '2024-12-10 09:00:00', 3, 5, 27.00),
(2, 'Fine Collection Report - November 2024', 'Report of fine collection for November 2024', '2024-12-01 10:00:00', 3, 15, 45.50),
(3, 'Popular Books Report - Q4 2024', 'Report of most popular books in Q4 2024', '2024-12-31 11:00:00', 3, 20, 0.00),
(4, 'User Activity Report - December 2024', 'Report of user activity for December 2024', '2024-12-31 12:00:00', 3, 30, 0.00),
(5, 'Loan Statistics Report - 2024', 'Annual loan statistics for 2024', '2024-12-31 13:00:00', 3, 100, 0.00);

-- =====================================================
-- END OF SAMPLE DATA
-- ===================================================== 