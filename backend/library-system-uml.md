# Library Management System - UML Class Diagram

```mermaid
classDiagram
    %% Core Entities
    class User {
        +Long id
        +String firstName
        +String lastName
        +String email
        +Library library
        +List~Loan~ loans
        +List~Reservation~ reservations
        +getFullName() String
        +canBorrowBooks() boolean
        +getCurrentLoans() List~Loan~
        +getReservationHistory() List~Reservation~
        +getCurrentLoanCount() int
        +getActiveReservations() List~Reservation~
    }

    class Book {
        +Long id
        +String title
        +String isbn
        +Integer availableCopies
        +Integer totalCopies
        +Author author
        +List~BookCopy~ bookCopies
        +isAvailable() boolean
        +borrowCopy() boolean
        +returnCopy() void
        +getAllCopies() List~BookCopy~
        +getCurrentReservations() List~Reservation~
        +getDetailedInfo() String
    }

    class BookCopy {
        +Long id
        +String barcode
        +Book book
        +Library library
        +Boolean isAvailable
        +BookCopyStatus status
        +String location
        +String notes
        +List~Loan~ loans
        +checkOut() void
        +returnCopy() void
        +markAsDamaged() void
        +sendToMaintenance(String reason) void
        +getCurrentLoan() Loan
        +isOnLoan() boolean
    }

    class Loan {
        +Long id
        +User user
        +BookCopy bookCopy
        +LocalDateTime loanDate
        +LocalDate dueDate
        +LocalDateTime returnDate
        +isOverdue() boolean
        +isActive() boolean
        +getDaysOverdue() long
        +returnBook(LocalDateTime returnDateTime) void
        +returnBook() void
        +extendDueDate(int days) void
        +calculateFine() BigDecimal
        +getLoanDurationDays() long
    }

    class Reservation {
        +Long id
        +User user
        +Book book
        +LocalDateTime reservationDate
        +LocalDate expiryDate
        +ReservationStatus status
        +Integer queuePosition
        +LocalDateTime notificationSentDate
        +String notes
        +isActive() boolean
        +isExpired() boolean
        +fulfill(LocalDateTime notificationDate) void
        +cancel(String reason) void
        +expire() void
        +extendExpiry(int additionalDays) void
        +getDaysUntilExpiry() long
        +getEstimatedWaitDays(int averageLoanDays) int
    }

    class Staff {
        +Long id
        +String firstName
        +String lastName
        +String email
        +String position
        +String department
        +String employeeId
        +LocalDate hireDate
        +LocalDate terminationDate
        +EmploymentStatus employmentStatus
        +Library library
        +Staff supervisor
        +LocalDateTime createdDate
        +LocalDateTime lastUpdated
        +getFullName() String
        +isActive() boolean
        +searchBooks(String title) List~Book~
        +checkAvailability(Long bookId) boolean
        +processReturn(Long bookCopyId) boolean
        +registerUser(User user) boolean
        +isCurrentlyEmployed() boolean
        +getYearsOfService() long
        +terminate(LocalDate terminationDate, String reason) void
        +putOnLeave(String leaveReason) void
        +returnFromLeave() void
    }

    class Library {
        +Long id
        +String name
        +String address
        +String city
        +List~BookCopy~ bookCopies
        +List~User~ members
        +List~Staff~ staff
        +getFullAddress() String
        +getAvailableBooks() List~Book~
        +getMemberCount() int
        +getBookCopyCount() int
        +getStaffCount() int
    }

    class Author {
        +Long id
        +String firstName
        +String lastName
        +List~Book~ books
        +getFullName() String
        +getBooksWritten() List~Book~
    }

    %% Enums
    class BookCopyStatus {
        <<enumeration>>
        AVAILABLE
        CHECKED_OUT
        RESERVED
        MAINTENANCE
        LOST
        DAMAGED
    }

    class ReservationStatus {
        <<enumeration>>
        ACTIVE
        FULFILLED
        CANCELLED
        EXPIRED
    }

    class EmploymentStatus {
        <<enumeration>>
        ACTIVE
        ON_LEAVE
        SUSPENDED
        TERMINATED
        RETIRED
    }

    %% Relationships
    Library ||--o{ User : "has_members"
    Library ||--o{ Staff : "employs"
    Library ||--o{ BookCopy : "holdings"
    
    Author ||--o{ Book : "writes"
    Book ||--o{ BookCopy : "has_copies"
    
    User ||--o{ Loan : "borrows"
    User ||--o{ Reservation : "reserves"
    
    Book ||--o{ Reservation : "reserved"
    BookCopy ||--o{ Loan : "is_loaned"
    
    Staff ||--o{ Staff : "supervises"
    
    %% Enum associations
    BookCopy --> BookCopyStatus : "has_status"
    Reservation --> ReservationStatus : "has_status"
    Staff --> EmploymentStatus : "has_status"
```

## 📋 **Entity Summary:**

### **Core Entities (8):**
1. **User** - Library members with borrowing capabilities
2. **Book** - Abstract book representation with copy management
3. **BookCopy** - Physical book copies with unique barcodes
4. **Loan** - Borrowing transaction records
5. **Reservation** - Book reservation system
6. **Staff** - Library employees with roles
7. **Library** - Physical library locations
8. **Author** - Book authors

### **Enumerations (3):**
1. **BookCopyStatus** - Tracks copy availability states
2. **ReservationStatus** - Manages reservation lifecycle
3. **EmploymentStatus** - Staff employment states

### **Key Relationships:**
- **Library 1 → * User** (has_members)
- **Library 1 → * Staff** (employs)
- **Library 1 → * BookCopy** (holdings)
- **Author 1 → * Book** (writes)
- **Book 1 → * BookCopy** (has_copies)
- **User 1 → * Loan** (borrows)
- **User 1 → * Reservation** (reserves)
- **Book 1 → * Reservation** (reserved)
- **BookCopy 1 → * Loan** (is_loaned)
- **Staff 1 → * Staff** (supervises)

### **Business Logic Methods:**
- **Copy Management**: `borrowCopy()`, `returnCopy()`, `isAvailable()`
- **Loan Operations**: `isOverdue()`, `calculateFine()`, `extendDueDate()`
- **Reservation System**: `isActive()`, `fulfill()`, `cancel()`
- **Staff Management**: `isActive()`, `processReturn()`, `registerUser()`
- **Library Operations**: `getAvailableBooks()`, `getMemberCount()` 