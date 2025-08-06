package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Staff entity representing library employees. */
@Entity
@Table(name = "staff")
public class Staff extends PanacheEntity {

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name must not exceed 100 characters")
  @Column(name = "first_name", nullable = false, length = 100)
  public String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name must not exceed 100 characters")
  @Column(name = "last_name", nullable = false, length = 100)
  public String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  @Column(name = "email", nullable = false, unique = true)
  public String email;

  @NotBlank(message = "Position is required")
  @Size(max = 100, message = "Position must not exceed 100 characters")
  @Column(name = "position", nullable = false, length = 100)
  public String position;

  @Size(max = 100, message = "Department must not exceed 100 characters")
  @Column(name = "department", length = 100)
  public String department;

  @Size(max = 50, message = "Employee ID must not exceed 50 characters")
  @Column(name = "employee_id", unique = true, length = 50)
  public String employeeId;

  @NotNull(message = "Hire date is required")
  @Column(name = "hire_date", nullable = false)
  public LocalDate hireDate = LocalDate.now();

  @Column(name = "termination_date")
  public LocalDate terminationDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "employment_status")
  public EmploymentStatus employmentStatus = EmploymentStatus.ACTIVE;

  @NotNull(message = "Library is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id", nullable = false)
  public Library library;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supervisor_id")
  public Staff supervisor;

  @Column(name = "created_date")
  public LocalDateTime createdDate = LocalDateTime.now();

  @Column(name = "last_updated")
  public LocalDateTime lastUpdated = LocalDateTime.now();

  /** Employment status enumeration. */
  public enum EmploymentStatus {
    ACTIVE,
    ON_LEAVE,
    SUSPENDED,
    TERMINATED,
    RETIRED
  }

  /** Default constructor for JPA. */
  public Staff() {}

  /**
   * Constructor with essential fields.
   *
   * @param firstName the staff member's first name
   * @param lastName the staff member's last name
   * @param email the staff member's email
   * @param position the staff member's position
   * @param library the library where the staff member works
   */
  public Staff(String firstName, String lastName, String email, String position, Library library) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.position = position;
    this.library = library;
  }

  /**
   * Gets the full name of the staff member.
   *
   * @return the full name as "firstName lastName"
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Checks if the staff member is currently active.
   *
   * @return true if employment status is active
   */
  public boolean isActive() {
    return employmentStatus == EmploymentStatus.ACTIVE;
  }

  /**
   * Searches for books by title.
   *
   * @param title the title to search for
   * @return list of books matching the title
   */
  public List<Book> searchBooks(String title) {
    if (title == null || title.trim().isEmpty()) {
      return List.of();
    }
    return Book.list("LOWER(title) LIKE LOWER(?1)", "%" + title.trim() + "%");
  }

  /**
   * Checks the availability of a specific book.
   *
   * @param bookId the book ID to check
   * @return true if the book is available for borrowing
   */
  public boolean checkAvailability(Long bookId) {
    Book book = Book.findById(bookId);
    if (book == null) {
      return false;
    }

    // Find an available copy of the book
    return BookCopy.find("book = ?1 and status = ?2", book, BookCopy.BookCopyStatus.AVAILABLE)
        .firstResultOptional()
        .isPresent();
  }

  /**
   * Processes the return of a book copy.
   *
   * @param bookCopyId the ID of the book copy being returned
   * @return true if the return was processed successfully
   */
  public boolean processReturn(Long bookCopyId) {
    BookCopy bookCopy = BookCopy.findById(bookCopyId);
    if (bookCopy == null || bookCopy.isAvailable()) {
      return false;
    }

    // Find the active loan for this book copy
    List<Loan> activeLoans = Loan.list("bookCopy = ?1 AND returnDate IS NULL", bookCopy);
    if (activeLoans.isEmpty()) {
      return false;
    }

    Loan loan = activeLoans.get(0);
    loan.returnBook();
    bookCopy.returnCopy();

    return true;
  }

  /**
   * Registers a new user in the system.
   *
   * @param user the user to register
   * @return true if registration was successful
   */
  public boolean registerUser(User user) {
    try {
      // Create a library membership for the user
      LibraryMembership membership = new LibraryMembership(user, this.library);
      membership.persist();
      user.persist();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks if the staff member is currently employed.
   *
   * @return true if not terminated or retired
   */
  public boolean isCurrentlyEmployed() {
    return employmentStatus != EmploymentStatus.TERMINATED
        && employmentStatus != EmploymentStatus.RETIRED;
  }

  /**
   * Gets the number of years of service.
   *
   * @return years of service
   */
  public long getYearsOfService() {
    LocalDate endDate = terminationDate != null ? terminationDate : LocalDate.now();
    return hireDate.until(endDate).getYears();
  }

  /**
   * Terminates the staff member's employment.
   *
   * @param terminationDate the termination date
   * @param reason the reason for termination
   */
  public void terminate(LocalDate terminationDate, String reason) {
    this.terminationDate = terminationDate;
    this.employmentStatus = EmploymentStatus.TERMINATED;
    this.lastUpdated = LocalDateTime.now();
  }

  /**
   * Sets the staff member on leave.
   *
   * @param leaveReason reason for leave
   */
  public void putOnLeave(String leaveReason) {
    this.employmentStatus = EmploymentStatus.ON_LEAVE;
    this.lastUpdated = LocalDateTime.now();
  }

  /** Returns the staff member from leave. */
  public void returnFromLeave() {
    this.employmentStatus = EmploymentStatus.ACTIVE;
    this.lastUpdated = LocalDateTime.now();
  }

  @Override
  public String toString() {
    return "Staff{"
        + "id="
        + id
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", position='"
        + position
        + '\''
        + ", employmentStatus="
        + employmentStatus
        + ", library="
        + (library != null ? library.name : "null")
        + '}';
  }
}
