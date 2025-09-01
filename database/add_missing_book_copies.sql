-- =====================================================
-- Script: add_missing_book_copies.sql
-- Purpose: Add missing book_copies for books with ids 5 through 31
-- Notes:
--   - Supports both schema variants: (status_id INT) or (status VARCHAR) on book_copies
--   - Generates new barcodes continuing from the current max (e.g., BC048, ...)
--   - Inserts copies until COUNT(book_copies) == books.total_copies for each book
--   - Assigns new copies to library_id = 1 with AVAILABLE status
--   - Skips books that do not exist
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

    DECLARE @nextBarcodeNum INT = ISNULL(
        (SELECT MAX(TRY_CAST(SUBSTRING(barcode, 3, 10) AS INT)) FROM book_copies),
        0
    ) + 1;

    DECLARE @bookId BIGINT = 5;
    WHILE @bookId <= 31
    BEGIN
        IF EXISTS (SELECT 1 FROM books WHERE id = @bookId)
        BEGIN
            DECLARE @totalCopies INT = (SELECT total_copies FROM books WHERE id = @bookId);
            DECLARE @existingCopies INT = (SELECT COUNT(*) FROM book_copies WHERE book_id = @bookId);
            DECLARE @toAdd INT = ISNULL(@totalCopies, 0) - ISNULL(@existingCopies, 0);

            WHILE @toAdd > 0
            BEGIN
                DECLARE @barcode VARCHAR(100) = CONCAT('BC', RIGHT(CONCAT('000', @nextBarcodeNum), 3));

                IF (@hasStatusText = 1)
                BEGIN
                    EXEC sp_executesql 
                        N'INSERT INTO book_copies (barcode, book_id, library_id, status, location) VALUES (@p1, @p2, 1, @p3, @p4);',
                        N'@p1 VARCHAR(100), @p2 BIGINT, @p3 VARCHAR(50), @p4 VARCHAR(100)',
                        @p1=@barcode, @p2=@bookId, @p3='AVAILABLE', @p4='Auto - Added';
                END
                ELSE IF (@hasStatusId = 1)
                BEGIN
                    EXEC sp_executesql 
                        N'INSERT INTO book_copies (barcode, book_id, library_id, status_id, location) VALUES (@p1, @p2, 1, 1, @p4);',
                        N'@p1 VARCHAR(100), @p2 BIGINT, @p4 VARCHAR(100)',
                        @p1=@barcode, @p2=@bookId, @p4='Auto - Added';
                END

                SET @nextBarcodeNum = @nextBarcodeNum + 1;
                SET @toAdd = @toAdd - 1;
            END
        END

        SET @bookId = @bookId + 1;
    END

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    DECLARE @errMsg NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @errSeverity INT = ERROR_SEVERITY();
    DECLARE @errState INT = ERROR_STATE();
    RAISERROR(@errMsg, @errSeverity, @errState);
END CATCH; 