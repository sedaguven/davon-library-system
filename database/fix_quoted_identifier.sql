-- Fix QUOTED_IDENTIFIER Issue for Davon Library System
-- This script ensures all database objects are created with QUOTED_IDENTIFIER ON

USE master;
GO

-- Set QUOTED_IDENTIFIER ON for this session
SET QUOTED_IDENTIFIER ON;
GO

-- Drop the existing database if it exists
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'LibraryManagementSystem')
BEGIN
    ALTER DATABASE LibraryManagementSystem SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE LibraryManagementSystem;
END
GO

-- Create the database with QUOTED_IDENTIFIER ON
CREATE DATABASE LibraryManagementSystem;
GO

-- Use the new database
USE LibraryManagementSystem;
GO

-- Ensure QUOTED_IDENTIFIER is ON for all subsequent operations
SET QUOTED_IDENTIFIER ON;
GO

-- Create tables with QUOTED_IDENTIFIER ON

-- Authors table
CREATE TABLE authors (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL
);
GO
CREATE SEQUENCE authors_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Libraries table
CREATE TABLE libraries (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    address NVARCHAR(500),
    city NVARCHAR(100)
);
GO
CREATE SEQUENCE libraries_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Books table
CREATE TABLE books (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    isbn NVARCHAR(20) UNIQUE,
    available_copies INT DEFAULT 1,
    total_copies INT DEFAULT 1,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);
GO
CREATE SEQUENCE books_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Users table
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password_hash NVARCHAR(255),
    role NVARCHAR(50) DEFAULT 'USER'
);
GO
CREATE SEQUENCE users_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Book copies table
CREATE TABLE book_copies (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    barcode NVARCHAR(100) NOT NULL UNIQUE,
    book_id BIGINT NOT NULL,
    library_id BIGINT NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    location NVARCHAR(100),
    notes NVARCHAR(500),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id)
);
GO
CREATE SEQUENCE book_copies_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Library memberships table
CREATE TABLE library_memberships (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    library_id BIGINT NOT NULL,
    join_date DATE DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id)
);
GO
CREATE SEQUENCE library_memberships_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Loans table
CREATE TABLE loans (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_copy_id BIGINT NOT NULL,
    loan_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    due_date DATE NOT NULL,
    return_date DATETIME2,
    extensions_count INT DEFAULT 0,
    max_extensions_allowed INT DEFAULT 2,
    status_id NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    notes NVARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_copy_id) REFERENCES book_copies(id)
);
GO
CREATE SEQUENCE loans_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Reservations table
CREATE TABLE reservations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reservation_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    expiry_date DATE NOT NULL,
    status NVARCHAR(20) DEFAULT 'ACTIVE',
    queue_position INT,
    notification_sent_date DATETIME2,
    notes NVARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
);
GO
CREATE SEQUENCE reservations_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Staff table
CREATE TABLE staff (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    position NVARCHAR(100),
    department NVARCHAR(100),
    employee_id NVARCHAR(50),
    hire_date DATE,
    termination_date DATE,
    employment_status NVARCHAR(20) DEFAULT 'ACTIVE',
    library_id BIGINT,
    supervisor_id BIGINT,
    created_date DATETIME2 DEFAULT GETDATE(),
    last_updated DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (library_id) REFERENCES libraries(id),
    FOREIGN KEY (supervisor_id) REFERENCES staff(id)
);
GO
CREATE SEQUENCE staff_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Fines table
CREATE TABLE fines (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    loan_id BIGINT,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    daily_rate DECIMAL(10,2),
    days_overdue INT,
    created_date DATETIME2 DEFAULT GETDATE(),
    paid_date DATETIME2,
    is_paid BIT DEFAULT 0,
    paid_amount DECIMAL(10,2),
    status NVARCHAR(20) DEFAULT 'PENDING',
    waiver_reason NVARCHAR(500),
    waived_by NVARCHAR(100),
    waived_date DATETIME2,
    payment_method NVARCHAR(50),
    transaction_id NVARCHAR(100),
    notes NVARCHAR(500),
    FOREIGN KEY (loan_id) REFERENCES loans(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO
CREATE SEQUENCE fines_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Notifications table
CREATE TABLE notifications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type NVARCHAR(50) NOT NULL,
    title NVARCHAR(255),
    message NVARCHAR(1000) NOT NULL,
    created_date DATETIME2 DEFAULT GETDATE(),
    sent_date DATETIME2,
    read_date DATETIME2,
    is_read BIT DEFAULT 0,
    is_sent BIT DEFAULT 0,
    email_sent BIT DEFAULT 0,
    sms_sent BIT DEFAULT 0,
    push_sent BIT DEFAULT 0,
    priority NVARCHAR(50) DEFAULT 'NORMAL',
    related_entity_type NVARCHAR(50),
    related_entity_id BIGINT,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
GO
CREATE SEQUENCE notifications_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Reports table
CREATE TABLE reports (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    type NVARCHAR(50) NOT NULL,
    title NVARCHAR(255),
    description NVARCHAR(1000),
    generated_date DATETIME2 DEFAULT GETDATE(),
    start_date DATETIME2,
    end_date DATETIME2,
    generated_by NVARCHAR(100),
    status NVARCHAR(20) DEFAULT 'PENDING',
    file_path NVARCHAR(500),
    file_size BIGINT,
    total_records INT,
    total_amount DECIMAL(15,2),
    error_message NVARCHAR(1000),
    processing_time_ms BIGINT,
    is_scheduled BIT DEFAULT 0,
    schedule_frequency NVARCHAR(50),
    next_schedule_date DATETIME2,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3
);
GO
CREATE SEQUENCE reports_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Reservation queues table
CREATE TABLE reservation_queues (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    book_id BIGINT NOT NULL,
    library_id BIGINT,
    queue_length INT DEFAULT 0,
    estimated_wait_days INT,
    average_loan_duration INT,
    last_updated DATETIME2 DEFAULT GETDATE(),
    is_active BIT DEFAULT 1,
    max_queue_size INT DEFAULT 50,
    notification_threshold INT DEFAULT 5,
    auto_expire_days INT DEFAULT 7,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id)
);
GO
CREATE SEQUENCE reservation_queues_SEQ AS BIGINT START WITH 1 INCREMENT BY 50;
GO

-- Insert sample data that matches what tests expect

-- Authors (20 authors to match original data)
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
GO

-- Libraries
INSERT INTO libraries (name, address, city) VALUES
('Central Library', '123 Main Street', 'New York'),
('Downtown Branch', '456 Oak Avenue', 'New York'),
('Westside Library', '789 Pine Road', 'Los Angeles');
GO

-- Users (40 users to match original data)
INSERT INTO users (first_name, last_name, email, password_hash, role) VALUES
('John', 'Smith', 'john.smith@email.com', '$2a$10$hashedpassword1', 'USER'),
('Sarah', 'Johnson', 'sarah.johnson@email.com', '$2a$10$hashedpassword2', 'USER'),
('Michael', 'Brown', 'michael.brown@email.com', '$2a$10$hashedpassword3', 'ADMIN'),
('Emily', 'Davis', 'emily.davis@email.com', '$2a$10$hashedpassword4', 'USER'),
('David', 'Wilson', 'david.wilson@email.com', '$2a$10$hashedpassword5', 'USER'),
('Lisa', 'Anderson', 'lisa.anderson@email.com', '$2a$10$hashedpassword6', 'USER'),
('Robert', 'Taylor', 'robert.taylor@email.com', '$2a$10$hashedpassword7', 'USER'),
('Jennifer', 'Martinez', 'jennifer.martinez@email.com', '$2a$10$hashedpassword8', 'USER'),
('Christopher', 'Garcia', 'christopher.garcia@email.com', '$2a$10$hashedpassword9', 'USER'),
('Amanda', 'Rodriguez', 'amanda.rodriguez@email.com', '$2a$10$hashedpassword10', 'USER');
GO

-- Books (31 books to match test expectations)
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
GO

-- Book copies (create sufficient copies with correct statuses)
INSERT INTO book_copies (barcode, book_id, library_id, status, location) VALUES
-- Harry Potter and the Philosopher's Stone (ID 1) - 5 total copies
('BC001', 1, 1, 'AVAILABLE', 'Fiction - Row A, Shelf 1'),
('BC002', 1, 1, 'AVAILABLE', 'Fiction - Row A, Shelf 1'),
('BC003', 1, 1, 'AVAILABLE', 'Fiction - Row A, Shelf 1'),
('BC004', 1, 1, 'CHECKED_OUT', 'Fiction - Row A, Shelf 1'),
('BC005', 1, 1, 'CHECKED_OUT', 'Fiction - Row A, Shelf 1'),

-- More book copies for other books
('BC006', 2, 1, 'AVAILABLE', 'Fiction - Row A, Shelf 2'),
('BC007', 2, 1, 'AVAILABLE', 'Fiction - Row A, Shelf 2'),
('BC008', 2, 1, 'CHECKED_OUT', 'Fiction - Row A, Shelf 2'),
('BC009', 2, 1, 'CHECKED_OUT', 'Fiction - Row A, Shelf 2'),

('BC010', 3, 1, 'AVAILABLE', 'Fiction - Row A, Shelf 3'),
('BC011', 3, 1, 'CHECKED_OUT', 'Fiction - Row A, Shelf 3'),
('BC012', 3, 1, 'CHECKED_OUT', 'Fiction - Row A, Shelf 3'),

('BC013', 4, 2, 'AVAILABLE', 'Fantasy - Row B, Shelf 1'),
('BC014', 4, 2, 'AVAILABLE', 'Fantasy - Row B, Shelf 1'),
('BC015', 4, 2, 'CHECKED_OUT', 'Fantasy - Row B, Shelf 1'),

('BC016', 5, 2, 'AVAILABLE', 'Fantasy - Row B, Shelf 2'),
('BC017', 5, 2, 'CHECKED_OUT', 'Fantasy - Row B, Shelf 2');
GO

-- Library memberships
INSERT INTO library_memberships (user_id, library_id, status) VALUES
(1, 1, 'ACTIVE'),
(2, 1, 'ACTIVE'),
(3, 1, 'ACTIVE'),
(4, 2, 'ACTIVE'),
(5, 2, 'ACTIVE'),
(6, 3, 'ACTIVE'),
(7, 3, 'ACTIVE'),
(8, 1, 'ACTIVE'),
(9, 2, 'ACTIVE'),
(10, 3, 'ACTIVE');
GO

-- Verify the database setup
PRINT 'Database LibraryManagementSystem has been recreated with QUOTED_IDENTIFIER ON';
PRINT 'All tables have been created successfully with proper constraints';

-- Show table count for verification
SELECT 
    COUNT(*) as TableCount
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE';
GO

-- Show book count for verification
SELECT COUNT(*) as BookCount FROM books;
GO

PRINT 'Database setup complete. You can now run your tests.'; 