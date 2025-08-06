-- =====================================================
-- Library Management System - Common Queries
-- =====================================================

-- =====================================================
-- BOOK SEARCH AND CATALOG QUERIES
-- =====================================================

-- 1. Search books by title, author, or ISBN
SELECT DISTINCT
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
WHERE b.title LIKE '%Harry Potter%'  -- Replace with search term
   OR a.first_name + ' ' + a.last_name LIKE '%Rowling%'  -- Replace with author name
   OR b.isbn LIKE '%978-0-7475-3269-9%'  -- Replace with ISBN
ORDER BY b.title;

-- 2. Find available books at a specific library
SELECT DISTINCT
    b.title,
    a.first_name + ' ' + a.last_name AS author_name,
    b.available_copies,
    bc.location,
    l.name AS library_name
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copies bc ON b.id = bc.book_id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE l.id = 1  -- Replace with library ID
  AND b.available_copies > 0
  AND bc.is_available = 1
  AND bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'AVAILABLE')
ORDER BY b.title;

-- 3. Find books by a specific author
SELECT DISTINCT
    b.title,
    b.isbn,
    b.available_copies,
    b.total_copies,
    l.name AS library_name
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copies bc ON b.id = bc.book_id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE a.first_name = 'J.K.' AND a.last_name = 'Rowling'  -- Replace with author name
ORDER BY b.title;

-- 4. Find books with low availability (less than 2 copies available)
SELECT DISTINCT
    b.title,
    a.first_name + ' ' + a.last_name AS author_name,
    b.available_copies,
    b.total_copies,
    l.name AS library_name
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copies bc ON b.id = bc.book_id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE b.available_copies < 2
ORDER BY b.available_copies, b.title;

-- =====================================================
-- USER AND MEMBERSHIP QUERIES
-- =====================================================

-- 5. Find user's current loans
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    b.title AS book_title,
    a.first_name + ' ' + a.last_name AS author_name,
    l.loan_date,
    l.due_date,
    DATEDIFF(day, GETDATE(), l.due_date) AS days_remaining
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
WHERE u.id = 1  -- Replace with user ID
  AND l.return_date IS NULL
ORDER BY l.due_date;

-- 6. Find user's loan history
SELECT 
    b.title AS book_title,
    a.first_name + ' ' + a.last_name AS author_name,
    l.loan_date,
    l.due_date,
    l.return_date,
    DATEDIFF(day, l.loan_date, l.return_date) AS days_borrowed
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
WHERE u.id = 1  -- Replace with user ID
  AND l.return_date IS NOT NULL
ORDER BY l.loan_date DESC;

-- 7. Find user's active reservations
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    b.title AS book_title,
    a.first_name + ' ' + a.last_name AS author_name,
    r.reservation_date,
    r.expiry_date,
    r.queue_position,
    DATEDIFF(day, GETDATE(), r.expiry_date) AS days_until_expiry
FROM reservations r
INNER JOIN users u ON r.user_id = u.id
INNER JOIN books b ON r.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
WHERE u.id = 26  -- Replace with user ID
  AND r.status_id = (SELECT id FROM reservation_status WHERE status_name = 'ACTIVE')
  AND r.expiry_date >= GETDATE()
ORDER BY r.reservation_date;

-- 8. Find library members by library
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    lm.join_date,
    ms.status_name AS membership_status
FROM library_memberships lm
INNER JOIN users u ON lm.user_id = u.id
INNER JOIN membership_status ms ON lm.status_id = ms.id
WHERE lm.library_id = 1  -- Replace with library ID
ORDER BY u.last_name, u.first_name;

-- 9. Find users with overdue books
SELECT DISTINCT
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    COUNT(l.id) AS overdue_books_count,
    SUM(f.amount) AS total_fines
FROM users u
INNER JOIN loans l ON u.id = l.user_id
LEFT JOIN fines f ON l.id = f.loan_id AND f.is_paid = 0
WHERE l.return_date IS NULL 
  AND l.due_date < GETDATE()
GROUP BY u.id, u.first_name, u.last_name, u.email
ORDER BY overdue_books_count DESC, total_fines DESC;

-- =====================================================
-- LOAN AND CIRCULATION QUERIES
-- =====================================================

-- 10. Find all overdue loans
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    b.title AS book_title,
    a.first_name + ' ' + a.last_name AS author_name,
    l.loan_date,
    l.due_date,
    DATEDIFF(day, l.due_date, GETDATE()) AS days_overdue,
    f.amount AS fine_amount
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
LEFT JOIN fines f ON l.id = f.loan_id AND f.is_paid = 0
WHERE l.return_date IS NULL 
  AND l.due_date < GETDATE()
ORDER BY days_overdue DESC;

-- 11. Find loans due soon (within 3 days)
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    b.title AS book_title,
    l.due_date,
    DATEDIFF(day, GETDATE(), l.due_date) AS days_until_due
FROM loans l
INNER JOIN users u ON l.user_id = u.id
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
INNER JOIN books b ON bc.book_id = b.id
WHERE l.return_date IS NULL 
  AND l.due_date BETWEEN GETDATE() AND DATEADD(day, 3, GETDATE())
ORDER BY l.due_date;

-- 12. Find most borrowed books
SELECT 
    b.title,
    a.first_name + ' ' + a.last_name AS author_name,
    COUNT(l.id) AS total_loans,
    COUNT(CASE WHEN l.return_date IS NULL THEN 1 END) AS active_loans
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copies bc ON b.id = bc.book_id
INNER JOIN loans l ON bc.id = l.book_copy_id
GROUP BY b.id, b.title, a.first_name, a.last_name
ORDER BY total_loans DESC;

-- 13. Find books that haven't been borrowed recently (last 6 months)
SELECT 
    b.title,
    a.first_name + ' ' + a.last_name AS author_name,
    b.available_copies,
    b.total_copies,
    MAX(l.loan_date) AS last_borrowed_date
FROM books b
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN loans l ON bc.id = l.book_copy_id
GROUP BY b.id, b.title, a.first_name, a.last_name, b.available_copies, b.total_copies
HAVING MAX(l.loan_date) IS NULL 
   OR MAX(l.loan_date) < DATEADD(month, -6, GETDATE())
ORDER BY last_borrowed_date;

-- =====================================================
-- FINANCIAL QUERIES
-- =====================================================

-- 14. Find outstanding fines by user
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    COUNT(f.id) AS fines_count,
    SUM(f.amount) AS total_amount,
    SUM(f.paid_amount) AS total_paid,
    SUM(f.amount - f.paid_amount) AS outstanding_amount
FROM users u
INNER JOIN fines f ON u.id = f.user_id
WHERE f.is_paid = 0
GROUP BY u.id, u.first_name, u.last_name, u.email
HAVING SUM(f.amount - f.paid_amount) > 0
ORDER BY outstanding_amount DESC;

-- 15. Find fine collection summary by month
SELECT 
    YEAR(f.created_date) AS year,
    MONTH(f.created_date) AS month,
    COUNT(f.id) AS fines_count,
    SUM(f.amount) AS total_fines,
    SUM(f.paid_amount) AS total_collected,
    SUM(f.amount - f.paid_amount) AS outstanding_amount
FROM fines f
WHERE f.created_date >= DATEADD(year, -1, GETDATE())
GROUP BY YEAR(f.created_date), MONTH(f.created_date)
ORDER BY year DESC, month DESC;

-- 16. Find users with high fine amounts
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    SUM(f.amount) AS total_fines,
    SUM(f.paid_amount) AS total_paid,
    SUM(f.amount - f.paid_amount) AS outstanding_amount,
    COUNT(f.id) AS fines_count
FROM users u
INNER JOIN fines f ON u.id = f.user_id
GROUP BY u.id, u.first_name, u.last_name, u.email
HAVING SUM(f.amount) > 10.00
ORDER BY total_fines DESC;

-- =====================================================
-- STAFF AND ADMINISTRATION QUERIES
-- =====================================================

-- 17. Find staff by library
SELECT 
    s.first_name + ' ' + s.last_name AS staff_name,
    s.email,
    s.position,
    s.department,
    s.hire_date,
    es.status_name AS employment_status,
    l.name AS library_name
FROM staff s
INNER JOIN employment_status es ON s.employment_status_id = es.id
INNER JOIN libraries l ON s.library_id = l.id
WHERE s.library_id = 1  -- Replace with library ID
ORDER BY s.last_name, s.first_name;

-- 18. Find staff hierarchy
SELECT 
    s.first_name + ' ' + s.last_name AS staff_name,
    s.position,
    sup.first_name + ' ' + sup.last_name AS supervisor_name,
    sup.position AS supervisor_position
FROM staff s
LEFT JOIN staff sup ON s.supervisor_id = sup.id
ORDER BY s.last_name, s.first_name;

-- 19. Find staff by employment status
SELECT 
    s.first_name + ' ' + s.last_name AS staff_name,
    s.email,
    s.position,
    s.hire_date,
    s.termination_date,
    es.status_name AS employment_status,
    l.name AS library_name
FROM staff s
INNER JOIN employment_status es ON s.employment_status_id = es.id
INNER JOIN libraries l ON s.library_id = l.id
WHERE es.status_name = 'ACTIVE'  -- Replace with status
ORDER BY s.hire_date;

-- =====================================================
-- INVENTORY AND MAINTENANCE QUERIES
-- =====================================================

-- 20. Find book copies by status
SELECT 
    b.title,
    bc.barcode,
    bcs.status_name AS copy_status,
    bc.location,
    l.name AS library_name,
    bc.is_available
FROM book_copies bc
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN book_copy_status bcs ON bc.status_id = bcs.id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE bcs.status_name = 'MAINTENANCE'  -- Replace with status
ORDER BY l.name, b.title;

-- 21. Find lost or damaged books
SELECT 
    b.title,
    a.first_name + ' ' + a.last_name AS author_name,
    bc.barcode,
    bcs.status_name AS copy_status,
    l.name AS library_name,
    bc.notes
FROM book_copies bc
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN book_copy_status bcs ON bc.status_id = bcs.id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE bcs.status_name IN ('LOST', 'DAMAGED')
ORDER BY l.name, b.title;

-- 22. Find books that need maintenance
SELECT 
    b.title,
    bc.barcode,
    bc.location,
    l.name AS library_name,
    bc.notes,
    bc.created_date
FROM book_copies bc
INNER JOIN books b ON bc.book_id = b.id
INNER JOIN libraries l ON bc.library_id = l.id
WHERE bc.status_id = (SELECT id FROM book_copy_status WHERE status_name = 'MAINTENANCE')
ORDER BY bc.created_date;

-- =====================================================
-- RESERVATION QUERIES
-- =====================================================

-- 23. Find active reservations by book
SELECT 
    b.title,
    a.first_name + ' ' + a.last_name AS author_name,
    COUNT(r.id) AS reservation_count,
    rq.queue_length,
    rq.estimated_wait_days
FROM reservations r
INNER JOIN books b ON r.book_id = b.id
INNER JOIN authors a ON b.author_id = a.id
INNER JOIN reservation_queues rq ON b.id = rq.book_id
WHERE r.status_id = (SELECT id FROM reservation_status WHERE status_name = 'ACTIVE')
  AND r.expiry_date >= GETDATE()
GROUP BY b.id, b.title, a.first_name, a.last_name, rq.queue_length, rq.estimated_wait_days
ORDER BY reservation_count DESC;

-- 24. Find expired reservations
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    b.title AS book_title,
    r.reservation_date,
    r.expiry_date,
    DATEDIFF(day, r.expiry_date, GETDATE()) AS days_expired
FROM reservations r
INNER JOIN users u ON r.user_id = u.id
INNER JOIN books b ON r.book_id = b.id
WHERE r.status_id = (SELECT id FROM reservation_status WHERE status_name = 'ACTIVE')
  AND r.expiry_date < GETDATE()
ORDER BY r.expiry_date;

-- =====================================================
-- REPORTING QUERIES
-- =====================================================

-- 25. Monthly loan statistics
SELECT 
    YEAR(l.loan_date) AS year,
    MONTH(l.loan_date) AS month,
    COUNT(l.id) AS total_loans,
    COUNT(CASE WHEN l.return_date IS NULL THEN 1 END) AS active_loans,
    COUNT(CASE WHEN l.due_date < GETDATE() AND l.return_date IS NULL THEN 1 END) AS overdue_loans,
    AVG(DATEDIFF(day, l.loan_date, l.return_date)) AS avg_loan_duration
FROM loans l
WHERE l.loan_date >= DATEADD(year, -1, GETDATE())
GROUP BY YEAR(l.loan_date), MONTH(l.loan_date)
ORDER BY year DESC, month DESC;

-- 26. Library usage statistics
SELECT 
    l.name AS library_name,
    l.city,
    COUNT(DISTINCT lm.user_id) AS total_members,
    COUNT(DISTINCT s.id) AS total_staff,
    COUNT(DISTINCT bc.id) AS total_book_copies,
    COUNT(DISTINCT CASE WHEN bc.is_available = 1 THEN bc.id END) AS available_copies
FROM libraries l
LEFT JOIN library_memberships lm ON l.id = lm.library_id AND lm.status_id = (SELECT id FROM membership_status WHERE status_name = 'ACTIVE')
LEFT JOIN staff s ON l.id = s.library_id AND s.employment_status_id = (SELECT id FROM employment_status WHERE status_name = 'ACTIVE')
LEFT JOIN book_copies bc ON l.id = bc.library_id
GROUP BY l.id, l.name, l.city
ORDER BY total_members DESC;

-- 27. Popular authors
SELECT 
    a.first_name + ' ' + a.last_name AS author_name,
    COUNT(b.id) AS books_count,
    SUM(b.total_copies) AS total_copies,
    SUM(b.available_copies) AS available_copies,
    COUNT(l.id) AS total_loans
FROM authors a
INNER JOIN books b ON a.id = b.author_id
LEFT JOIN book_copies bc ON b.id = bc.book_id
LEFT JOIN loans l ON bc.id = l.book_copy_id
GROUP BY a.id, a.first_name, a.last_name
ORDER BY total_loans DESC;

-- 28. User activity summary
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    COUNT(l.id) AS total_loans,
    COUNT(CASE WHEN l.return_date IS NULL THEN 1 END) AS active_loans,
    COUNT(CASE WHEN l.due_date < GETDATE() AND l.return_date IS NULL THEN 1 END) AS overdue_loans,
    COUNT(r.id) AS total_reservations,
    SUM(f.amount) AS total_fines,
    SUM(f.paid_amount) AS total_paid_fines
FROM users u
LEFT JOIN loans l ON u.id = l.user_id
LEFT JOIN reservations r ON u.id = r.user_id
LEFT JOIN fines f ON u.id = f.user_id
GROUP BY u.id, u.first_name, u.last_name, u.email
ORDER BY total_loans DESC;

-- =====================================================
-- NOTIFICATION QUERIES
-- =====================================================

-- 29. Find unread notifications by user
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    nt.type_name AS notification_type,
    np.priority_name AS priority,
    n.title,
    n.message,
    n.created_date
FROM notifications n
INNER JOIN users u ON n.user_id = u.id
INNER JOIN notification_type nt ON n.type_id = nt.id
INNER JOIN notification_priority np ON n.priority_id = np.id
WHERE n.is_read = 0
  AND u.id = 1  -- Replace with user ID
ORDER BY np.id DESC, n.created_date DESC;

-- 30. Find urgent notifications
SELECT 
    u.first_name + ' ' + u.last_name AS user_name,
    u.email,
    nt.type_name AS notification_type,
    n.title,
    n.message,
    n.created_date
FROM notifications n
INNER JOIN users u ON n.user_id = u.id
INNER JOIN notification_type nt ON n.type_id = nt.id
INNER JOIN notification_priority np ON n.priority_id = np.id
WHERE np.priority_name IN ('HIGH', 'URGENT')
  AND n.is_read = 0
ORDER BY np.id DESC, n.created_date;

-- =====================================================
-- COMPLEX ANALYTICAL QUERIES
-- =====================================================

-- 31. Find peak borrowing times
SELECT 
    DATEPART(hour, l.loan_date) AS hour_of_day,
    DATEPART(weekday, l.loan_date) AS day_of_week,
    COUNT(l.id) AS loan_count
FROM loans l
WHERE l.loan_date >= DATEADD(month, -3, GETDATE())
GROUP BY DATEPART(hour, l.loan_date), DATEPART(weekday, l.loan_date)
ORDER BY loan_count DESC;

-- 32. Find book return patterns
SELECT 
    DATEPART(weekday, l.return_date) AS day_of_week,
    DATEPART(hour, l.return_date) AS hour_of_day,
    COUNT(l.id) AS return_count,
    AVG(DATEDIFF(day, l.loan_date, l.return_date)) AS avg_loan_duration
FROM loans l
WHERE l.return_date IS NOT NULL
  AND l.return_date >= DATEADD(month, -3, GETDATE())
GROUP BY DATEPART(weekday, l.return_date), DATEPART(hour, l.return_date)
ORDER BY return_count DESC;

-- 33. Find seasonal borrowing trends
SELECT 
    DATEPART(month, l.loan_date) AS month,
    DATENAME(month, l.loan_date) AS month_name,
    COUNT(l.id) AS loan_count,
    COUNT(DISTINCT l.user_id) AS unique_users,
    COUNT(DISTINCT bc.book_id) AS unique_books
FROM loans l
INNER JOIN book_copies bc ON l.book_copy_id = bc.id
WHERE l.loan_date >= DATEADD(year, -1, GETDATE())
GROUP BY DATEPART(month, l.loan_date), DATENAME(month, l.loan_date)
ORDER BY month;

-- 34. Find library efficiency metrics
SELECT 
    l.name AS library_name,
    COUNT(DISTINCT lm.user_id) AS active_members,
    COUNT(DISTINCT bc.id) AS total_copies,
    COUNT(l.id) AS total_loans,
    CAST(COUNT(l.id) AS FLOAT) / NULLIF(COUNT(DISTINCT lm.user_id), 0) AS loans_per_member,
    CAST(COUNT(l.id) AS FLOAT) / NULLIF(COUNT(DISTINCT bc.id), 0) AS loans_per_copy,
    AVG(DATEDIFF(day, l.loan_date, l.return_date)) AS avg_loan_duration
FROM libraries l
LEFT JOIN library_memberships lm ON l.id = lm.library_id AND lm.status_id = (SELECT id FROM membership_status WHERE status_name = 'ACTIVE')
LEFT JOIN book_copies bc ON l.id = bc.library_id
LEFT JOIN loans l ON bc.id = l.book_copy_id AND l.loan_date >= DATEADD(month, -6, GETDATE())
GROUP BY l.id, l.name
ORDER BY loans_per_member DESC;

-- =====================================================
-- END OF QUERIES
-- ===================================================== 