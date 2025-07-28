# Library Management System - Updated UML Class Diagram

```mermaid
classDiagram
    %% Core Entities
    class User {
        +Long id
        +String firstName
        +String lastName
        +String email
        +List~LibraryMembership~ memberships
        +List~Loan~ loans
        +List~Reservation~ reservations
        +getFullName() String
        +canBorrowBooks() boolean
        +getCurrentLoans() List~Loan~
        +getReservationHistory() List~Reservation~
        +getCurrentLoanCount() int
        +getActiveReservations() List~Reservation~
        +getLibraries() List~Library~
        +isMemberOf(Library) boolean
        +getActiveMemberships() List~LibraryMembership~
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
        +returnCopy() boolean
        +getAllCopies() List~BookCopy~
        +getCurrentReservations() List~Reservation~
        +getAvailableCopies() List~BookCopy~
        +updateAvailability() void
    }

    class BookCopy {
        +Long id
        +String barcode
        +Book book
        +Library library
        +boolean isAvailable
        +BookCopyStatus status
        +String location
        +String notes
        +List~Loan~ loans
        +checkOut() boolean
        +returnCopy() boolean
        +markAsDamaged() void
        +sendToMaintenance() void
        +getCurrentLoan() Loan
        +isOnLoan() boolean
        +getLoanHistory() List~Loan~
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
        +getDaysOverdue() int
        +returnBook() void
        +extendDueDate(int) boolean
        +calculateFine() BigDecimal
        +getLoanDuration() long
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
        +cancel() void
        +extend() boolean
        +isExpired() boolean
        +getDaysUntilExpiry() int
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
        +searchBooks(String) List~Book~
        +checkAvailability(Long) boolean
        +processReturn(Long) boolean
        +registerUser(User) boolean
        +isCurrentlyEmployed() boolean
        +getYearsOfService() long
        +terminate(LocalDate, String) void
        +putOnLeave(String) void
        +returnFromLeave() void
    }

    class Library {
        +Long id
        +String name
        +String address
        +String city
        +List~BookCopy~ bookCopies
        +List~LibraryMembership~ memberships
        +List~Staff~ staff
        +getFullAddress() String
        +getAvailableBooks() List~Book~
        +getMemberCount() int
        +getBookCopyCount() int
        +getStaffCount() int
        +getActiveMembers() List~User~
        +getActiveMemberships() List~LibraryMembership~
        +getAvailableBookCopies() List~BookCopy~
    }

    class Author {
        +Long id
        +String firstName
        +String lastName
        +List~Book~ books
        +getFullName() String
        +getBooksWritten() List~Book~
        +getBookCount() int
    }

    %% New Association Entity
    class LibraryMembership {
        +Long id
        +User user
        +Library library
        +LocalDate joinDate
        +MembershipStatus status
        +LibraryMembership(User, Library)
        +isActive() boolean
        +suspend() void
        +activate() void
        +cancel() void
        +getMembershipDuration() long
    }

    %% Enumerations
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

    class MembershipStatus {
        <<enumeration>>
        ACTIVE
        SUSPENDED
        CANCELLED
        EXPIRED
    }

    %% Relationships
    User ||--o{ LibraryMembership : "has many"
    Library ||--o{ LibraryMembership : "has many"
    LibraryMembership }o--|| User : "belongs to"
    LibraryMembership }o--|| Library : "belongs to"

    User ||--o{ Loan : "borrows"
    User ||--o{ Reservation : "reserves"
    
    Book ||--o{ BookCopy : "has many"
    BookCopy }o--|| Book : "belongs to"
    
    BookCopy ||--o{ Loan : "has many"
    Loan }o--|| BookCopy : "borrowed as"
    
    Book ||--o{ Reservation : "has many"
    Reservation }o--|| Book : "reserved for"
    
    Author ||--o{ Book : "writes"
    Book }o--|| Author : "written by"
    
    Library ||--o{ BookCopy : "houses"
    BookCopy }o--|| Library : "located at"
    
    Library ||--o{ Staff : "employs"
    Staff }o--|| Library : "works at"
    
    Staff ||--o{ Staff : "supervises"
    Staff }o--|| Staff : "supervised by"

    %% Enumeration relationships
    BookCopyStatus ||--o{ BookCopy : "has"
    ReservationStatus ||--o{ Reservation : "has"
    EmploymentStatus ||--o{ Staff : "has"
    MembershipStatus ||--o{ LibraryMembership : "has"
```

## Key Changes in the Updated UML Diagram

### 🔄 **New Many-to-Many Relationship**

1. **LibraryMembership Entity**: New association class that manages the many-to-many relationship between User and Library
2. **Fields in LibraryMembership**:
   - `id`: Primary key
   - `user`: Reference to User entity
   - `library`: Reference to Library entity  
   - `joinDate`: When the user joined the library
   - `status`: Current membership status (ACTIVE, SUSPENDED, CANCELLED, EXPIRED)

### 🎯 **Updated Relationships**

1. **User ↔ Library**: Now many-to-many through LibraryMembership
   - A user can be a member of multiple libraries
   - A library can have multiple users as members
   - Each membership has its own status and join date

2. **Staff ↔ Library**: Remains one-to-many
   - Staff still work at only one library (as requested)
   - Each staff member belongs to a single library

### 📊 **New Methods Added**

1. **User Entity**:
   - `getLibraries()`: Returns all libraries the user is a member of
   - `isMemberOf(Library)`: Checks if user is a member of a specific library
   - `getActiveMemberships()`: Returns all active library memberships

2. **Library Entity**:
   - `getActiveMembers()`: Returns all active members of the library
   - `getActiveMemberships()`: Returns all active memberships

3. **LibraryMembership Entity**:
   - `isActive()`: Checks if membership is active
   - `suspend()`, `activate()`, `cancel()`: Status management methods
   - `getMembershipDuration()`: Calculates how long the user has been a member

### 🏗️ **Database Structure**

The new structure allows for:
- **Flexible membership management**: Users can join multiple libraries
- **Membership status tracking**: Active, suspended, cancelled memberships
- **Audit trail**: Join dates and status changes
- **Library-specific operations**: Each library can manage its own members independently

This design provides much more flexibility while maintaining data integrity and allowing for complex library network scenarios. 