package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Loan entity representing book borrowing transactions. */
@Entity
@Table(name = "loans")
public class Loan extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @NotNull(message = "User is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  public User user;

  @NotNull(message = "BookCopy is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_copy_id", nullable = false)
  public BookCopy bookCopy;

  @NotNull(message = "Loan date is required")
  @Column(name = "loan_date", nullable = false)
  public LocalDateTime loanDate = LocalDateTime.now();

  @NotNull(message = "Due date is required")
  @Column(name = "due_date", nullable = false)
  public LocalDate dueDate;

  @Column(name = "return_date")
  public LocalDateTime returnDate;

  @Column(name = "extensions_count")
  public Integer extensionsCount = 0;

  @Column(name = "max_extensions_allowed")
  public Integer maxExtensionsAllowed = 2;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_id", nullable = false)
  public LoanStatus status = LoanStatus.ACTIVE;

  @Column(name = "notes", length = 500)
  public String notes;

  @JsonIgnore
  @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Fine> fines;

  /** Loan status enumeration. */
  public enum LoanStatus {
    ACTIVE,
    RETURNED,
    OVERDUE,
    LOST,
    DAMAGED
  }

  /** Default constructor for JPA. */
  public Loan() {}

  /**
   * Constructor with essential fields.
   *
   * @param user the borrowing user
   * @param bookCopy the borrowed book copy
   * @param dueDate the due date for return
   */
  public Loan(User user, BookCopy bookCopy, LocalDate dueDate) {
    this.user = user;
    this.bookCopy = bookCopy;
    this.dueDate = dueDate;
  }

  /**
   * Constructor with loan period in days.
   *
   * @param user the borrowing user
   * @param bookCopy the borrowed book copy
   * @param loanPeriodDays the loan period in days
   */
  public Loan(User user, BookCopy bookCopy, int loanPeriodDays) {
    this.user = user;
    this.bookCopy = bookCopy;
    this.dueDate = LocalDate.now().plusDays(loanPeriodDays);
  }

  /**
   * Checks if the loan is currently overdue.
   *
   * @return true if the loan is overdue
   */
  public boolean isOverdue() {
    return returnDate == null && LocalDate.now().isAfter(dueDate);
  }

  /**
   * Checks if the loan is active (not returned).
   *
   * @return true if the loan is active
   */
  public boolean isActive() {
    return returnDate == null && status == LoanStatus.ACTIVE;
  }

  /**
   * Gets the number of days overdue.
   *
   * @return the number of days overdue, or 0 if not overdue
   */
  public long getDaysOverdue() {
    if (!isOverdue()) {
      return 0;
    }
    return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
  }

  /**
   * Marks the loan as returned.
   *
   * @param returnDateTime the return date and time
   */
  public void returnBook(LocalDateTime returnDateTime) {
    this.returnDate = returnDateTime;
    this.status = LoanStatus.RETURNED;
    if (bookCopy != null) {
      bookCopy.returnCopy();
    }
  }

  /** Marks the loan as returned with current date/time. */
  public void returnBook() {
    returnBook(LocalDateTime.now());
  }

  /**
   * Extends the due date by the specified number of days.
   *
   * @param days the number of days to extend
   * @return true if extension was successful
   */
  public boolean extendDueDate(int days) {
    if (extensionsCount >= maxExtensionsAllowed) {
      return false;
    }

    this.dueDate = this.dueDate.plusDays(days);
    this.extensionsCount++;
    return true;
  }

  /**
   * Gets the loan duration in days.
   *
   * @return the loan duration from loan date to due date
   */
  public long getLoanDurationDays() {
    return ChronoUnit.DAYS.between(loanDate.toLocalDate(), dueDate);
  }

  /**
   * Gets the actual loan duration (from loan date to return date or current date).
   *
   * @return the actual loan duration in days
   */
  public long getActualLoanDurationDays() {
    LocalDate endDate = returnDate != null ? returnDate.toLocalDate() : LocalDate.now();
    return ChronoUnit.DAYS.between(loanDate.toLocalDate(), endDate);
  }

  /**
   * Marks the loan as lost.
   *
   * @param notes additional notes about the loss
   */
  public void markAsLost(String notes) {
    this.status = LoanStatus.LOST;
    this.notes = notes;
  }

  /**
   * Marks the loan as damaged.
   *
   * @param notes additional notes about the damage
   */
  public void markAsDamaged(String notes) {
    this.status = LoanStatus.DAMAGED;
    this.notes = notes;
  }

  /**
   * Gets the current fine for this loan.
   *
   * @return the current fine if exists, null otherwise
   */
  public Fine getCurrentFine() {
    if (fines == null || fines.isEmpty()) {
      return null;
    }

    return fines.stream()
        .filter(fine -> fine.status == Fine.FineStatus.ACTIVE)
        .findFirst()
        .orElse(null);
  }

  /**
   * Checks if the loan has outstanding fines.
   *
   * @return true if the loan has unpaid fines
   */
  public boolean hasOutstandingFines() {
    Fine currentFine = getCurrentFine();
    return currentFine != null && !currentFine.isPaid;
  }

  /**
   * Gets the total outstanding fine amount.
   *
   * @return the total outstanding fine amount
   */
  public java.math.BigDecimal getTotalOutstandingFineAmount() {
    Fine currentFine = getCurrentFine();
    return currentFine != null ? currentFine.getRemainingAmount() : java.math.BigDecimal.ZERO;
  }

  /**
   * Checks if the loan can be extended.
   *
   * @return true if the loan can be extended
   */
  public boolean canBeExtended() {
    return isActive() && extensionsCount < maxExtensionsAllowed && !isOverdue();
  }

  /**
   * Gets the loan summary information.
   *
   * @return formatted loan information
   */
  public String getLoanSummary() {
    return String.format(
        "Loan: %s borrowed %s (Due: %s, Status: %s)",
        user != null ? user.getFullName() : "Unknown User",
        bookCopy != null && bookCopy.book != null ? bookCopy.book.title : "Unknown Book",
        dueDate,
        status);
  }

  /**
   * Calculates the fine amount for this loan.
   *
   * @return the calculated fine amount
   */
  public java.math.BigDecimal calculateFine() {
    if (returnDate != null || !isOverdue()) {
      return java.math.BigDecimal.ZERO;
    }

    long daysOverdue = getDaysOverdue();
    java.math.BigDecimal dailyRate = new java.math.BigDecimal("0.50");
    return dailyRate.multiply(java.math.BigDecimal.valueOf(daysOverdue));
  }

  /**
   * Gets the overdue status description.
   *
   * @return description of overdue status
   */
  public String getOverdueStatus() {
    if (!isOverdue()) {
      return "Not overdue";
    }

    long days = getDaysOverdue();
    if (days == 1) {
      return "1 day overdue";
    } else {
      return days + " days overdue";
    }
  }

  @Override
  public String toString() {
    return "Loan{"
        + "id="
        + id
        + ", user="
        + (user != null ? user.getFullName() : "null")
        + ", bookCopy="
        + (bookCopy != null ? bookCopy.barcode : "null")
        + ", loanDate="
        + loanDate
        + ", dueDate="
        + dueDate
        + ", returnDate="
        + returnDate
        + ", status="
        + status
        + ", extensionsCount="
        + extensionsCount
        + '}';
  }
}
