-- =====================================================
-- Library Management System Database Schema (COMPLETE)
-- =====================================================

-- =====================================================
-- ENUMERATION TABLES (for status and type values) - NO DEPENDENCIES
-- =====================================================

-- Book Copy Status
CREATE TABLE book_copy_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Loan Status
CREATE TABLE loan_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Reservation Status
CREATE TABLE reservation_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Membership Status
CREATE TABLE membership_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Employment Status
CREATE TABLE employment_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Fine Status
CREATE TABLE fine_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Notification Type
CREATE TABLE notification_type (
    id INT PRIMARY KEY IDENTITY(1,1),
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Notification Priority
CREATE TABLE notification_priority (
    id INT PRIMARY KEY IDENTITY(1,1),
    priority_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Report Type
CREATE TABLE report_type (
    id INT PRIMARY KEY IDENTITY(1,1),
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Report Status
CREATE TABLE report_status (
    id INT PRIMARY KEY IDENTITY(1,1),
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

GO

-- =====================================================
-- CORE ENTITY TABLES (NO FOREIGN KEYS)
-- =====================================================

-- Authors table
CREATE TABLE authors (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE()
);

-- Libraries table
CREATE TABLE libraries (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE()
);

-- Users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE()
);

GO

-- =====================================================
-- TABLES WITH SINGLE FOREIGN KEY DEPENDENCIES
-- =====================================================

-- Books table (depends on authors)
CREATE TABLE books (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    available_copies INT DEFAULT 1,
    total_copies INT DEFAULT 1,
    author_id BIGINT NOT NULL,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

-- Staff table (depends on employment_status, libraries, and self-reference)
CREATE TABLE staff (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    position VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    employee_id VARCHAR(50) UNIQUE,
    hire_date DATE NOT NULL DEFAULT GETDATE(),
    termination_date DATE,
    employment_status_id INT NOT NULL,
    library_id BIGINT NOT NULL,
    supervisor_id BIGINT,
    created_date DATETIME2 DEFAULT GETDATE(),
    last_updated DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (employment_status_id) REFERENCES employment_status(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id),
    FOREIGN KEY (supervisor_id) REFERENCES staff(id)
);

GO

-- =====================================================
-- TABLES WITH MULTIPLE FOREIGN KEY DEPENDENCIES
-- =====================================================

-- Library Memberships table (depends on users, libraries, membership_status)
CREATE TABLE library_memberships (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    library_id BIGINT NOT NULL,
    join_date DATE NOT NULL,
    status_id INT NOT NULL,
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id),
    FOREIGN KEY (status_id) REFERENCES membership_status(id),
    UNIQUE(user_id, library_id)
);

-- Book Copies table (depends on books, libraries, book_copy_status)
CREATE TABLE book_copies (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    barcode VARCHAR(100) NOT NULL UNIQUE,
    book_id BIGINT NOT NULL,
    library_id BIGINT NOT NULL,
    status_id INT NOT NULL,
    location VARCHAR(100),
    notes VARCHAR(500),
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id),
    FOREIGN KEY (status_id) REFERENCES book_copy_status(id)
);

-- Reservation Queues table (depends on books, libraries)
CREATE TABLE reservation_queues (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    book_id BIGINT NOT NULL,
    library_id BIGINT NOT NULL,
    queue_length INT DEFAULT 0,
    estimated_wait_days INT DEFAULT 0,
    average_loan_duration INT DEFAULT 14,
    last_updated DATETIME2 DEFAULT GETDATE(),
    is_active BIT DEFAULT 1,
    max_queue_size INT DEFAULT 50,
    notification_threshold INT DEFAULT 3,
    auto_expire_days INT DEFAULT 7,
    created_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (library_id) REFERENCES libraries(id),
    UNIQUE(book_id, library_id)
);

GO

-- =====================================================
-- TRANSACTION TABLES (DEPEND ON MULTIPLE TABLES)
-- =====================================================

-- Loans table (depends on users, book_copies, loan_status)
CREATE TABLE loans (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    book_copy_id BIGINT NOT NULL,
    loan_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    due_date DATE NOT NULL,
    return_date DATETIME2,
    extensions_count INT DEFAULT 0,
    max_extensions_allowed INT DEFAULT 2,
    status_id INT NOT NULL,
    notes VARCHAR(500),
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_copy_id) REFERENCES book_copies(id),
    FOREIGN KEY (status_id) REFERENCES loan_status(id)
);

-- Reservations table (depends on users, books, reservation_status)
CREATE TABLE reservations (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    reservation_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    expiry_date DATE NOT NULL,
    status_id INT NOT NULL,
    queue_position INT,
    notification_sent_date DATETIME2,
    notes VARCHAR(500),
    created_date DATETIME2 DEFAULT GETDATE(),
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (status_id) REFERENCES reservation_status(id)
);

GO

-- =====================================================
-- FINANCIAL TABLES (DEPEND ON LOANS)
-- =====================================================

-- Fines table (depends on loans, users, fine_status)
CREATE TABLE fines (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    loan_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    daily_rate DECIMAL(5,2) DEFAULT 0.50,
    days_overdue INT,
    created_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    paid_date DATETIME2,
    is_paid BIT DEFAULT 0,
    paid_amount DECIMAL(10,2) DEFAULT 0.00,
    status_id INT NOT NULL,
    waiver_reason VARCHAR(500),
    waived_by VARCHAR(100),
    waived_date DATETIME2,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    notes VARCHAR(500),
    updated_date DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (loan_id) REFERENCES loans(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (status_id) REFERENCES fine_status(id)
);

-- =====================================================
-- NOTIFICATION TABLES (DEPEND ON USERS)
-- =====================================================

-- Notifications table (depends on users, notification_type, notification_priority)
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user_id BIGINT NOT NULL,
    type_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message VARCHAR(1000),
    created_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    sent_date DATETIME2,
    read_date DATETIME2,
    is_read BIT DEFAULT 0,
    is_sent BIT DEFAULT 0,
    priority_id INT NOT NULL,
    related_entity_id BIGINT,
    related_entity_type VARCHAR(50),
    email_sent BIT DEFAULT 0,
    sms_sent BIT DEFAULT 0,
    push_sent BIT DEFAULT 0,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (type_id) REFERENCES notification_type(id),
    FOREIGN KEY (priority_id) REFERENCES notification_priority(id)
);

-- =====================================================
-- REPORTING TABLES (DEPEND ON ENUMERATION TABLES)
-- =====================================================

-- Reports table (depends on report_type, report_status)
CREATE TABLE reports (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    type_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    generated_date DATETIME2 NOT NULL DEFAULT GETDATE(),
    start_date DATETIME2,
    end_date DATETIME2,
    generated_by VARCHAR(100),
    status_id INT NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT,
    total_records INT DEFAULT 0,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    error_message VARCHAR(1000),
    processing_time_ms BIGINT,
    is_scheduled BIT DEFAULT 0,
    schedule_frequency VARCHAR(100),
    next_schedule_date DATETIME2,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    FOREIGN KEY (type_id) REFERENCES report_type(id),
    FOREIGN KEY (status_id) REFERENCES report_status(id)
);

GO

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

-- Books indexes
CREATE INDEX idx_books_author_id ON books(author_id);
CREATE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_title ON books(title);

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_name ON users(first_name, last_name);

-- Library Memberships indexes
CREATE INDEX idx_library_memberships_user_id ON library_memberships(user_id);
CREATE INDEX idx_library_memberships_library_id ON library_memberships(library_id);
CREATE INDEX idx_library_memberships_status_id ON library_memberships(status_id);

-- Staff indexes
CREATE INDEX idx_staff_email ON staff(email);
CREATE INDEX idx_staff_employee_id ON staff(employee_id);
CREATE INDEX idx_staff_library_id ON staff(library_id);
CREATE INDEX idx_staff_employment_status_id ON staff(employment_status_id);
CREATE INDEX idx_staff_supervisor_id ON staff(supervisor_id);

-- Loans indexes
CREATE INDEX idx_loans_user_id ON loans(user_id);
CREATE INDEX idx_loans_book_copy_id ON loans(book_copy_id);
CREATE INDEX idx_loans_due_date ON loans(due_date);
CREATE INDEX idx_loans_status_id ON loans(status_id);
CREATE INDEX idx_loans_return_date ON loans(return_date);

-- Reservations indexes
CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_book_id ON reservations(book_id);
CREATE INDEX idx_reservations_status_id ON reservations(status_id);
CREATE INDEX idx_reservations_expiry_date ON reservations(expiry_date);

-- Book Copies indexes
CREATE INDEX idx_book_copies_barcode ON book_copies(barcode);
CREATE INDEX idx_book_copies_book_id ON book_copies(book_id);
CREATE INDEX idx_book_copies_library_id ON book_copies(library_id);
CREATE INDEX idx_book_copies_status_id ON book_copies(status_id);

-- Fines indexes
CREATE INDEX idx_fines_user_id ON fines(user_id);
CREATE INDEX idx_fines_loan_id ON fines(loan_id);
CREATE INDEX idx_fines_status_id ON fines(status_id);
CREATE INDEX idx_fines_is_paid ON fines(is_paid);

-- Notifications indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_type_id ON notifications(type_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_date ON notifications(created_date);

GO

-- =====================================================
-- CONSTRAINTS AND VALIDATIONS
-- =====================================================

-- Check constraints
ALTER TABLE books ADD CONSTRAINT chk_books_copies CHECK (available_copies >= 0 AND total_copies >= available_copies);
ALTER TABLE loans ADD CONSTRAINT chk_loans_dates CHECK (due_date >= CAST(loan_date AS DATE));
ALTER TABLE loans ADD CONSTRAINT chk_loans_extensions CHECK (extensions_count >= 0);
ALTER TABLE fines ADD CONSTRAINT chk_fines_amount CHECK (amount >= 0);
ALTER TABLE fines ADD CONSTRAINT chk_fines_paid_amount CHECK (paid_amount >= 0 AND paid_amount <= amount);
ALTER TABLE reservation_queues ADD CONSTRAINT chk_queue_length CHECK (queue_length >= 0);
ALTER TABLE reservation_queues ADD CONSTRAINT chk_max_queue_size CHECK (max_queue_size > 0);

GO

-- =====================================================
-- TRIGGERS FOR DATA INTEGRITY
-- =====================================================

-- Trigger to update book available copies when book copy status changes
CREATE TRIGGER tr_book_copies_status_update
ON book_copies
AFTER UPDATE
AS
BEGIN
    IF UPDATE(status_id)
    BEGIN
        UPDATE b
        SET available_copies = (
            SELECT COUNT(*)
            FROM book_copies bc
            WHERE bc.book_id = b.id 
            AND bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
        )
        FROM books b
        INNER JOIN inserted i ON b.id = i.book_id;
    END
END;

GO

-- Trigger to update book available copies when loans are created/updated
CREATE TRIGGER tr_loans_book_copy_update
ON loans
AFTER INSERT, UPDATE
AS
BEGIN
    IF UPDATE(return_date)
    BEGIN
        -- Update book copy availability when loan is returned
        UPDATE bc
        SET status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
        FROM book_copies bc
        INNER JOIN inserted i ON bc.id = i.book_copy_id
        WHERE i.return_date IS NOT NULL;
        
        -- Update book available copies count
        UPDATE b
        SET available_copies = (
            SELECT COUNT(*)
            FROM book_copies bc
            WHERE bc.book_id = b.id 
            AND bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
        )
        FROM books b
        INNER JOIN book_copies bc ON b.id = bc.book_id
        INNER JOIN inserted i ON bc.id = i.book_copy_id
        WHERE i.return_date IS NOT NULL;
    END
END;

GO

-- =====================================================
-- VIEWS FOR COMMON QUERIES
-- =====================================================

-- View for available books
CREATE VIEW v_available_books AS
SELECT 
    b.id,
    b.title,
    b.isbn,
    b.available_copies,
    b.total_copies,
    a.first_name + ' ' + a.last_name AS author_name,
    l.name AS library_name,
    l.city AS library_city
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copies bc ON b.id = bc.book_id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE b.available_copies > 0
AND bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE');

GO

-- View for overdue loans
CREATE VIEW v_overdue_loans AS
SELECT 
    l.id,
    u.first_name + ' ' + u.last_name AS user_name,
    u.email AS user_email,
    b.title AS book_title,
    a.first_name + ' ' + a.last_name AS author_name,
    l.loan_date,
    l.due_date,
    l.return_date,
    DATEDIFF(day, l.due_date, GETDATE()) AS days_overdue,
    ls.status_name AS loan_status
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN loan_status ls ON l.status_id = ls.id
WHERE l.return_date IS NULL 
AND l.due_date < GETDATE();

GO

-- View for active reservations
CREATE VIEW v_active_reservations AS
SELECT 
    r.id,
    u.first_name + ' ' + u.last_name AS user_name,
    u.email AS user_email,
    b.title AS book_title,
    a.first_name + ' ' + a.last_name AS author_name,
    r.reservation_date,
    r.expiry_date,
    r.queue_position,
    rs.status_name AS reservation_status
FROM reservations r
INNER JOIN users u ON r.user_id = u.id
INNER JOIN books b ON r.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN reservation_status rs ON r.status_id = rs.id
WHERE r.status_id = (SELECT id FROM reservation_status WHERE status_name = 'ACTIVE')
AND r.expiry_date >= GETDATE();

GO

-- View for outstanding fines
CREATE VIEW v_outstanding_fines AS
SELECT 
    f.id,
    u.first_name + ' ' + u.last_name AS user_name,
    u.email AS user_email,
    b.title AS book_title,
    f.amount,
    f.paid_amount,
    f.amount - f.paid_amount AS remaining_amount,
    f.days_overdue,
    f.created_date,
    fs.status_name AS fine_status
FROM fines f
INNER JOIN users u ON f.user_id = u.id
INNER JOIN loans l ON f.loan_id = l.id
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN fine_status fs ON f.status_id = fs.id
WHERE f.is_paid = 0
AND f.status_id = (SELECT id FROM fine_status WHERE status_name = 'ACTIVE');

GO

-- =====================================================
-- STORED PROCEDURES
-- =====================================================

-- Procedure to borrow a book
CREATE PROCEDURE sp_borrow_book
    @user_id BIGINT,
    @book_copy_id BIGINT,
    @loan_period_days INT = 14
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @due_date DATE = DATEADD(day, @loan_period_days, GETDATE());
    DECLARE @book_copy_available BIT;
    DECLARE @user_can_borrow BIT = 1;
    
    -- Check if book copy is available
    SELECT @book_copy_available = CASE WHEN status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE') THEN 1 ELSE 0 END
    FROM book_copies
    WHERE id = @book_copy_id;
    
    IF @book_copy_available = 0
    BEGIN
        RAISERROR('Book copy is not available for borrowing', 16, 1);
        RETURN;
    END
    
    -- Check if user can borrow more books
    SELECT @user_can_borrow = CASE 
        WHEN COUNT(*) >= 5 THEN 0 
        ELSE 1 
    END
    FROM loans
    WHERE user_id = @user_id 
    AND return_date IS NULL;
    
    IF @user_can_borrow = 0
    BEGIN
        RAISERROR('User has reached the maximum number of loans (5)', 16, 1);
        RETURN;
    END
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Create loan
        INSERT INTO loans (user_id, book_copy_id, due_date, status_id)
        VALUES (@user_id, @book_copy_id, @due_date, 
                (SELECT id FROM loan_status WHERE status_name = 'ACTIVE'));
        
        -- Update book copy status
        UPDATE book_copies
        SET status_id = (SELECT id FROM book_copy_status WHERE status_name = 'CHECKED_OUT')
        WHERE id = @book_copy_id;
        
        -- Update book available copies
        UPDATE b
        SET available_copies = (
            SELECT COUNT(*)
            FROM book_copies bc
            WHERE bc.book_id = b.id 
            AND bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
        )
        FROM books b
        INNER JOIN book_copies bc ON b.id = bc.book_id
        WHERE bc.id = @book_copy_id;
        
        COMMIT TRANSACTION;
        
        SELECT 'Book borrowed successfully' AS message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;

GO

-- Procedure to return a book
CREATE PROCEDURE sp_return_book
    @loan_id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @book_copy_id BIGINT;
    DECLARE @return_date DATETIME2 = GETDATE();
    
    -- Get book copy ID from loan
    SELECT @book_copy_id = book_copy_id
    FROM loans
    WHERE id = @loan_id AND return_date IS NULL;
    
    IF @book_copy_id IS NULL
    BEGIN
        RAISERROR('Loan not found or book already returned', 16, 1);
        RETURN;
    END
    
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Update loan
        UPDATE loans
        SET return_date = @return_date,
            status_id = (SELECT id FROM loan_status WHERE status_name = 'RETURNED')
        WHERE id = @loan_id;
        
        -- Update book copy status
        UPDATE book_copies
        SET status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
        WHERE id = @book_copy_id;
        
        -- Update book available copies
        UPDATE b
        SET available_copies = (
            SELECT COUNT(*)
            FROM book_copies bc
            WHERE bc.book_id = b.id 
            AND bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
        )
        FROM books b
        INNER JOIN book_copies bc ON b.id = bc.book_id
        WHERE bc.id = @book_copy_id;
        
        COMMIT TRANSACTION;
        
        SELECT 'Book returned successfully' AS message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;

GO

-- Procedure to search books
CREATE PROCEDURE sp_search_books
    @search_term VARCHAR(255) = NULL,
    @author_id BIGINT = NULL,
    @library_id BIGINT = NULL,
    @available_only BIT = 0
AS
BEGIN
    SET NOCOUNT ON;
    
    SELECT DISTINCT
        b.id,
        b.title,
        b.isbn,
        b.available_copies,
        b.total_copies,
        a.first_name + ' ' + a.last_name AS author_name,
        a.id AS author_id,
        l.name AS library_name,
        l.id AS library_id,
        l.city AS library_city
    FROM books b
    INNER JOIN authors a ON b.author_id = a.id
    INNER JOIN book_copies bc ON b.id = bc.book_id
    INNER JOIN libraries l ON bc.library_id = l.id
    WHERE (@search_term IS NULL OR 
           b.title LIKE '%' + @search_term + '%' OR 
           CONCAT(a.first_name, ' ', a.last_name) LIKE '%' + @search_term + '%' OR
           b.isbn LIKE '%' + @search_term + '%')
    AND (@author_id IS NULL OR a.id = @author_id)
    AND (@library_id IS NULL OR l.id = @library_id)
    AND (@available_only = 0 OR b.available_copies > 0)
    ORDER BY b.title;
END;

GO

-- =====================================================
-- END OF SCHEMA CREATION
-- ===================================================== 