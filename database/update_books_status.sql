-- Add status column to books
IF COL_LENGTH('books', 'status') IS NULL
BEGIN
    ALTER TABLE books ADD status VARCHAR(20) NULL;
END
GO

-- Backfill status based on available copies of book_copies
-- We consider a copy available when book_copies.status maps to AVAILABLE
WITH available_counts AS (
    SELECT bc.book_id AS book_id,
           SUM(CASE WHEN bc.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_copies
    FROM book_copies bc
    GROUP BY bc.book_id
)
UPDATE b
SET b.status = CASE WHEN ISNULL(ac.available_copies, 0) > 0 THEN 'AVAILABLE' ELSE 'UNAVAILABLE' END
FROM books b
LEFT JOIN available_counts ac ON ac.book_id = b.id;
GO

-- Default any remaining NULL statuses to UNAVAILABLE
UPDATE books SET status = 'UNAVAILABLE' WHERE status IS NULL;
GO

-- Enforce NOT NULL constraint
ALTER TABLE books ALTER COLUMN status VARCHAR(20) NOT NULL;
GO 