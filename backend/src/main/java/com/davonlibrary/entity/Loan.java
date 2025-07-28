package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/** Loan entity representing book borrowing transactions. */
@Entity
@Table(name = "loans")
public class Loan extends PanacheEntity {

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
    return returnDate == null;
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
   */
  public void extendDueDate(int days) {
    this.dueDate = this.dueDate.plusDays(days);
  }

  /**
   * Calculates the fine amount for overdue loans.
   *
   * @return the fine amount
   */
  public BigDecimal calculateFine() {
    if (!isOverdue()) {
      return BigDecimal.ZERO;
    }
    long daysOverdue = getDaysOverdue();
    BigDecimal dailyRate = new BigDecimal("0.50"); // Default daily fine rate
    return dailyRate.multiply(BigDecimal.valueOf(daysOverdue));
  }

  /**
   * Gets the loan duration in days.
   *
   * @return the loan duration from loan date to due date
   */
  public long getLoanDurationDays() {
    return ChronoUnit.DAYS.between(loanDate.toLocalDate(), dueDate);
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
        + '}';
  }
}
