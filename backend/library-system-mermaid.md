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
    
    Book ||--o{ Loan : "is_borrowed"
    Book ||--o{ Reservation : "reserved"
    
    BookCopy ||--o{ Loan : "is_loaned"
    BookCopy ||--o{ Reservation : "is_reserved"
    
    Staff ||--o{ Staff : "supervises"
    
    %% Enum associations
    BookCopy --> BookCopyStatus : "has_status"
    Reservation --> ReservationStatus : "has_status"
    Staff --> EmploymentStatus : "has_status"
```

## Key Features of the Library Management System:

### **Core Entities:**
- **User**: Library members who can borrow books and make reservations
- **Book**: Abstract representation of books with title, ISBN, and copy management
- **BookCopy**: Physical copies of books with unique barcodes and availability status
- **Loan**: Records of book borrowing transactions
- **Reservation**: Book reservations when copies are unavailable
- **Staff**: Library employees with various roles and permissions
- **Library**: Physical library locations
- **Author**: Book authors

### **Key Relationships:**
- **Library 1 → * User**: Libraries have multiple members
- **Library 1 → * Staff**: Libraries employ multiple staff members
- **Library 1 → * BookCopy**: Libraries hold multiple book copies
- **Author 1 → * Book**: Authors write multiple books
- **Book 1 → * BookCopy**: Books have multiple physical copies
- **User 1 → * Loan**: Users can have multiple active loans
- **User 1 → * Reservation**: Users can have multiple reservations
- **BookCopy 1 → * Loan**: Book copies can be loaned multiple times
- **Staff 1 → * Staff**: Staff can supervise other staff members

### **Business Logic:**
- **Copy Management**: Tracks available vs. total copies
- **Loan Tracking**: Manages due dates, returns, and overdue fines
- **Reservation System**: Queues for unavailable books
- **Staff Management**: Employment status and supervision hierarchy
- **Library Operations**: Member and inventory management 