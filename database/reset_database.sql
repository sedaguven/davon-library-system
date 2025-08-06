USE master;
GO

IF DB_ID('LibraryManagementSystem') IS NOT NULL
BEGIN
    ALTER DATABASE LibraryManagementSystem SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE LibraryManagementSystem;
END
GO

CREATE DATABASE LibraryManagementSystem;
GO

USE LibraryManagementSystem;
GO 