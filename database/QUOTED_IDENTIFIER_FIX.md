# Fix QUOTED_IDENTIFIER Issue

This document provides instructions to fix the `QUOTED_IDENTIFIER` issue in your Davon Library System.

## Problem
Your tests are failing with the error:
```
UPDATE failed because the following SET options have incorrect settings: 'QUOTED_IDENTIFIER'
```

This occurs when database objects were created with `QUOTED_IDENTIFIER OFF` but your application tries to access them with `QUOTED_IDENTIFIER ON`.

## Solution
Run the provided SQL script to recreate your database with consistent `QUOTED_IDENTIFIER ON` settings.

## Instructions

### Method 1: Using SQL Server Management Studio (SSMS)
1. Open SQL Server Management Studio
2. Connect to your SQL Server instance
3. Open the file `fix_quoted_identifier.sql`
4. Execute the script (F5 or click Execute)

### Method 2: Using sqlcmd (Command Line)
1. Open Command Prompt or PowerShell
2. Navigate to the database folder:
   ```bash
   cd /Users/sedaguven/davon-library-system/database
   ```
3. Run the script:
   ```bash
   sqlcmd -S localhost -E -i fix_quoted_identifier.sql
   ```
   
   Or if using SQL Server authentication:
   ```bash
   sqlcmd -S localhost -U sa -P LibraryDB123! -i fix_quoted_identifier.sql
   ```

### Method 3: Using Azure Data Studio
1. Open Azure Data Studio
2. Connect to your SQL Server instance
3. Open the file `fix_quoted_identifier.sql`
4. Run the script

## What the Script Does

1. **Drops the existing database** (with data backup warning)
2. **Recreates the database** with `QUOTED_IDENTIFIER ON`
3. **Creates all tables** with proper constraints and relationships
4. **Inserts sample data** for testing
5. **Verifies the setup** with a table count query

## Sample Data Included

The script includes sample data for:
- 5 authors
- 3 libraries  
- 5 users (including admin)
- 5 books
- 10 book copies
- 5 library memberships

This data is sufficient for running your tests.

## After Running the Script

1. **Verify the database** by checking that all tables exist
2. **Run your tests** again:
   ```bash
   cd ../backend
   mvn test
   ```

## Expected Results

After running this fix, you should see:
- ✅ All 285 tests passing
- ✅ No `QUOTED_IDENTIFIER` errors
- ✅ The `LibraryResourceTest.testBorrowOrReserveBook_Success` test passing

## Backup Warning

⚠️ **IMPORTANT**: This script will **drop your existing database**. If you have important data, make sure to backup your database first using:

```sql
BACKUP DATABASE LibraryManagementSystem 
TO DISK = 'C:\Backup\LibraryManagementSystem.bak'
```

## Troubleshooting

If you encounter any issues:

1. **Permission errors**: Make sure you have `sysadmin` or `dbcreator` privileges
2. **Connection issues**: Verify your SQL Server is running and accessible
3. **Database in use**: Close all connections to the database before running the script

## Verification

After running the script, you can verify it worked by running:

```sql
USE LibraryManagementSystem;
SELECT @@OPTIONS & 256 as QuotedIdentifierSetting;
-- Should return 256 (meaning QUOTED_IDENTIFIER is ON)
``` 