# Library Management System - Database Documentation

## Overview

This document provides comprehensive documentation for the Library Management System database design, including schema creation, sample data, and common queries.

## Database Architecture

### Design Principles

- **Normalized Design**: Follows 3NF to minimize data redundancy
- **Referential Integrity**: All relationships enforced with foreign keys
- **Audit Trail**: Includes created/updated timestamps for all entities
- **Status Management**: Uses enumeration tables for status values
- **Performance Optimization**: Includes appropriate indexes and constraints

### Database Schema

The database consists of 23 tables organized into the following categories:

#### 1. Enumeration Tables (9 tables)
- `book_copy_status` - Book copy availability status
- `loan_status` - Loan transaction status
- `reservation_status` - Reservation status
- `membership_status` - Library membership status
- `employment_status` - Staff employment status
- `fine_status` - Fine payment status
- `notification_type` - Types of notifications
- `notification_priority` - Notification priority levels
- `report_type` - Types of reports
- `report_status` - Report generation status

#### 2. Core Entity Tables (5 tables)
- `authors` - Book authors
- `libraries` - Library locations
- `books` - Book catalog
- `users` - Library members
- `staff` - Library employees

#### 3. Relationship Tables (2 tables)
- `library_memberships` - User-library relationships
- `book_copies` - Physical book copies

#### 4. Transaction Tables (3 tables)
- `loans` - Book borrowing transactions
- `reservations` - Book reservations
- `reservation_queues` - Reservation queue management

#### 5. Financial Tables (1 table)
- `fines` - Overdue book fines

#### 6. Notification Tables (1 table)
- `notifications` - User notifications

#### 7. Reporting Tables (1 table)
- `reports` - Generated reports

## File Structure

```
database/
├── schema.sql          # Database schema creation
├── sample_data.sql     # Sample data insertion
├── queries.sql         # Common queries and reports
└── README.md          # This documentation
```

## Installation and Setup

### Prerequisites

- Microsoft SQL Server (2016 or later)
- SQL Server Management Studio (SSMS) or Azure Data Studio
- Appropriate permissions to create databases and tables

### Setup Instructions

1. **Create Database**
   ```sql
   CREATE DATABASE LibraryManagementSystem;
   USE LibraryManagementSystem;
   ```

2. **Run Schema Creation**
   ```bash
   # Execute schema.sql in your SQL client
   # This creates all tables, indexes, constraints, triggers, views, and stored procedures
   ```

3. **Insert Sample Data**
   ```bash
   # Execute sample_data.sql in your SQL client
   # This populates the database with sample data for testing
   ```

4. **Verify Installation**
   ```sql
   -- Check if all tables were created
   SELECT TABLE_NAME 
   FROM INFORMATION_SCHEMA.TABLES 
   WHERE TABLE_TYPE = 'BASE TABLE';
   
   -- Check if sample data was inserted
   SELECT COUNT(*) FROM authors;
   SELECT COUNT(*) FROM books;
   SELECT COUNT(*) FROM users;
   ```

## Key Features

### 1. Book Management
- **Multi-copy Support**: Each book can have multiple physical copies
- **Location Tracking**: Book copies are assigned to specific libraries and locations
- **Status Management**: Tracks availability, maintenance, lost, and damaged status
- **ISBN Validation**: Supports ISBN-10 and ISBN-13 formats

### 2. User Management
- **Multi-library Membership**: Users can be members of multiple libraries
- **Membership Status**: Tracks active, suspended, expired, and cancelled memberships
- **Loan Limits**: Enforces maximum loan limits per user
- **Fine Tracking**: Comprehensive fine management system

### 3. Loan Management
- **Flexible Loan Periods**: Configurable loan durations
- **Extension Support**: Allows loan extensions with limits
- **Overdue Tracking**: Automatic overdue detection and fine calculation
- **Return Processing**: Streamlined book return workflow

### 4. Reservation System
- **Queue Management**: Tracks reservation queues for popular books
- **Expiry Handling**: Automatic reservation expiry
- **Notification System**: Notifies users when reserved books become available

### 5. Staff Management
- **Hierarchical Structure**: Supports supervisor-subordinate relationships
- **Employment Status**: Tracks active, on leave, suspended, terminated, and retired status
- **Department Organization**: Organizes staff by departments and positions

### 6. Financial Management
- **Fine Calculation**: Automatic fine calculation based on overdue days
- **Payment Tracking**: Tracks partial and full payments
- **Waiver Support**: Allows fine waivers with reason tracking
- **Financial Reporting**: Comprehensive financial reports

### 7. Notification System
- **Multiple Types**: Supports various notification types (overdue, due soon, etc.)
- **Priority Levels**: Urgent, high, normal, and low priority notifications
- **Delivery Tracking**: Tracks email, SMS, and push notification delivery
- **Retry Logic**: Implements retry mechanisms for failed notifications

### 8. Reporting System
- **Scheduled Reports**: Supports scheduled report generation
- **Multiple Formats**: Supports various report types
- **Status Tracking**: Tracks report generation status
- **Error Handling**: Comprehensive error handling and retry logic

## Database Relationships

### Entity Relationship Diagram

```
Authors (1) ←→ (N) Books (1) ←→ (N) BookCopies (N) ←→ (1) Libraries
    ↑                                                      ↑
    |                                                      |
    |                                                      |
Users (1) ←→ (N) LibraryMemberships (N) ←→ (1) Libraries
    ↑                                                      ↑
    |                                                      |
    |                                                      |
Loans (N) ←→ (1) BookCopies ←→ (1) Libraries
    ↑
    |
Fines (N) ←→ (1) Loans

Reservations (N) ←→ (1) Books
    ↑
    |
ReservationQueues (1) ←→ (1) Books

Notifications (N) ←→ (1) Users

Staff (N) ←→ (1) Libraries
    ↑
    |
Staff (N) ←→ (1) Staff (Supervisor)

Reports (N) ←→ (1) ReportTypes
```

### Key Relationships

1. **One-to-Many**:
   - Author → Books
   - Library → BookCopies
   - Library → Staff
   - User → Loans
   - User → Reservations
   - Book → BookCopies

2. **Many-to-Many** (implemented as separate tables):
   - Users ↔ Libraries (via LibraryMemberships)
   - Books ↔ Libraries (via BookCopies)

3. **Self-Referencing**:
   - Staff → Staff (supervisor relationship)

## Performance Optimization

### Indexes

The database includes strategic indexes for optimal performance:

- **Primary Keys**: All tables have auto-incrementing primary keys
- **Foreign Keys**: Indexed for efficient joins
- **Search Fields**: Title, author name, ISBN, email, barcode
- **Date Fields**: Loan dates, due dates, created dates
- **Status Fields**: Various status columns for filtering

### Constraints

- **Check Constraints**: Validate data integrity (e.g., available copies ≤ total copies)
- **Unique Constraints**: Email addresses, ISBNs, barcodes, employee IDs
- **Foreign Key Constraints**: Ensure referential integrity
- **Default Values**: Sensible defaults for common fields

### Triggers

- **Book Copy Status Updates**: Automatically updates book available copies when copy status changes
- **Loan Return Processing**: Automatically updates book availability when loans are returned

## Views

The database includes several views for common queries:

1. **v_available_books**: Shows all available books with author and library information
2. **v_overdue_loans**: Lists all overdue loans with user and book details
3. **v_active_reservations**: Shows active reservations with user and book information
4. **v_outstanding_fines**: Lists all outstanding fines with user and book details

## Stored Procedures

### Core Procedures

1. **sp_borrow_book**: Processes book borrowing with validation
2. **sp_return_book**: Processes book returns with status updates
3. **sp_search_books**: Flexible book search with multiple criteria

### Usage Examples

```sql
-- Borrow a book
EXEC sp_borrow_book @user_id = 1, @book_copy_id = 3, @loan_period_days = 14;

-- Return a book
EXEC sp_return_book @loan_id = 1;

-- Search for books
EXEC sp_search_books @search_term = 'Harry Potter', @available_only = 1;
```

## Common Queries

The `queries.sql` file contains 34 common queries organized into categories:

### Book Search and Catalog (4 queries)
- Search by title, author, or ISBN
- Find available books by library
- Find books by specific author
- Find books with low availability

### User and Membership (5 queries)
- User's current loans
- User's loan history
- User's active reservations
- Library members by library
- Users with overdue books

### Loan and Circulation (4 queries)
- All overdue loans
- Loans due soon
- Most borrowed books
- Books not borrowed recently

### Financial (3 queries)
- Outstanding fines by user
- Fine collection summary by month
- Users with high fine amounts

### Staff and Administration (3 queries)
- Staff by library
- Staff hierarchy
- Staff by employment status

### Inventory and Maintenance (3 queries)
- Book copies by status
- Lost or damaged books
- Books needing maintenance

### Reservation (2 queries)
- Active reservations by book
- Expired reservations

### Reporting (4 queries)
- Monthly loan statistics
- Library usage statistics
- Popular authors
- User activity summary

### Notifications (2 queries)
- Unread notifications by user
- Urgent notifications

### Complex Analytics (4 queries)
- Peak borrowing times
- Book return patterns
- Seasonal borrowing trends
- Library efficiency metrics

## Data Types and Sizes

### String Fields
- **Names**: VARCHAR(100) for first/last names
- **Titles**: VARCHAR(255) for book titles
- **Emails**: VARCHAR(255) with email validation
- **Addresses**: VARCHAR(500) for full addresses
- **ISBNs**: VARCHAR(20) for ISBN-10/13
- **Barcodes**: VARCHAR(100) for book copy barcodes

### Numeric Fields
- **IDs**: BIGINT for primary keys (supports large datasets)
- **Copies**: INT for book copy counts
- **Fines**: DECIMAL(10,2) for precise financial calculations
- **Dates**: DATETIME2 for high-precision timestamps

### Boolean Fields
- **Availability**: BIT for true/false flags
- **Status**: BIT for various status indicators

## Security Considerations

### Data Protection
- **Email Validation**: Enforced at database level
- **ISBN Validation**: Pattern matching for valid ISBNs
- **Date Validation**: Ensures logical date relationships
- **Amount Validation**: Prevents negative amounts

### Access Control
- **User Isolation**: Users can only access their own data
- **Staff Permissions**: Role-based access control
- **Audit Trail**: All changes tracked with timestamps

## Backup and Maintenance

### Recommended Backup Strategy
- **Full Backup**: Daily full database backup
- **Transaction Log Backup**: Every 15 minutes during business hours
- **Differential Backup**: Weekly differential backups

### Maintenance Tasks
- **Index Rebuilding**: Monthly index maintenance
- **Statistics Updates**: Weekly statistics updates
- **Data Archiving**: Quarterly archiving of old data
- **Performance Monitoring**: Continuous performance monitoring

## Troubleshooting

### Common Issues

1. **Foreign Key Violations**
   - Ensure referenced records exist before inserting
   - Use proper deletion order (child records first)

2. **Unique Constraint Violations**
   - Check for duplicate emails, ISBNs, or barcodes
   - Use appropriate error handling in applications

3. **Performance Issues**
   - Monitor query execution plans
   - Ensure indexes are being used effectively
   - Consider query optimization for complex reports

### Performance Monitoring

```sql
-- Check index usage
SELECT 
    OBJECT_NAME(i.object_id) AS table_name,
    i.name AS index_name,
    ius.user_seeks,
    ius.user_scans,
    ius.user_lookups
FROM sys.dm_db_index_usage_stats ius
INNER JOIN sys.indexes i ON ius.object_id = i.object_id 
    AND ius.index_id = i.index_id
WHERE ius.database_id = DB_ID();

-- Check table sizes
SELECT 
    t.name AS table_name,
    p.rows AS row_count,
    SUM(a.total_pages) * 8 AS total_size_kb
FROM sys.tables t
INNER JOIN sys.indexes i ON t.object_id = i.object_id
INNER JOIN sys.partitions p ON i.object_id = p.object_id 
    AND i.index_id = p.index_id
INNER JOIN sys.allocation_units a ON p.partition_id = a.container_id
GROUP BY t.name, p.rows
ORDER BY total_size_kb DESC;
```

## Future Enhancements

### Potential Improvements
1. **Full-Text Search**: Implement full-text search for book content
2. **Partitioning**: Partition large tables by date for better performance
3. **Compression**: Implement data compression for storage optimization
4. **Replication**: Set up database replication for high availability
5. **Audit Tables**: Implement comprehensive audit logging
6. **API Integration**: Prepare for REST API integration

### Scalability Considerations
- **Horizontal Partitioning**: Partition by library for multi-tenant scenarios
- **Read Replicas**: Implement read replicas for reporting workloads
- **Caching**: Implement application-level caching for frequently accessed data
- **Microservices**: Consider splitting into domain-specific databases

## Support and Documentation

### Additional Resources
- **Entity Relationship Diagrams**: Available in separate documentation
- **API Documentation**: When API layer is implemented
- **User Manuals**: For end-user training
- **Developer Guides**: For application developers

### Contact Information
For database-related questions or issues, please refer to the project documentation or contact the development team.

---

**Version**: 1.0  
**Last Updated**: December 2024  
**Compatibility**: SQL Server 2016+  
**License**: Project-specific license 