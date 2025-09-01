-- =====================================================
-- Script: sync_available_copies.sql
-- Purpose: Synchronize books.available_copies with count of AVAILABLE copies
-- Notes:
--   - Supports schema variants: book_copies.status (VARCHAR) or status_id (INT)
--   - Uses dynamic SQL to avoid compiling references to non-existent columns
--   - Assumes status_id=1 means AVAILABLE when status_id is present
-- =====================================================

SET NOCOUNT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @hasStatusId BIT = CASE WHEN EXISTS (
        SELECT 1 FROM sys.columns 
        WHERE object_id = OBJECT_ID('book_copies') 
          AND name = 'status_id'
    ) THEN 1 ELSE 0 END;

    DECLARE @hasStatusText BIT = CASE WHEN EXISTS (
        SELECT 1 FROM sys.columns 
        WHERE object_id = OBJECT_ID('book_copies') 
          AND name = 'status'
    ) THEN 1 ELSE 0 END;

    IF (@hasStatusId = 0 AND @hasStatusText = 0)
    BEGIN
        RAISERROR('Neither status_id nor status column exists on book_copies.', 16, 1);
    END

    DECLARE @sql NVARCHAR(MAX);

    IF (@hasStatusText = 1)
    BEGIN
        SET @sql = N'
            ;WITH available AS (
                SELECT c.book_id, COUNT(*) AS available_count
                FROM book_copies c
                WHERE c.status = ''AVAILABLE''
                GROUP BY c.book_id
            )
            UPDATE b
            SET b.available_copies = ISNULL(a.available_count, 0)
            FROM books b
            LEFT JOIN available a ON a.book_id = b.id;
        ';
    END
    ELSE IF (@hasStatusId = 1)
    BEGIN
        SET @sql = N'
            ;WITH available AS (
                SELECT c.book_id, COUNT(*) AS available_count
                FROM book_copies c
                WHERE c.status_id = 1
                GROUP BY c.book_id
            )
            UPDATE b
            SET b.available_copies = ISNULL(a.available_count, 0)
            FROM books b
            LEFT JOIN available a ON a.book_id = b.id;
        ';
    END

    EXEC sp_executesql @sql;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    DECLARE @errMsg NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @errSeverity INT = ERROR_SEVERITY();
    DECLARE @errState INT = ERROR_STATE();
    RAISERROR(@errMsg, @errSeverity, @errState);
END CATCH; 