package com.davonlibrary.repository;

import com.davonlibrary.entity.Loan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Repository for Loan entity operations and queries. */
@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {

  @Inject EntityManager em;

  public List<Loan> findByUser(Long userId) {
    return list("user.id", userId);
  }

  public List<Loan> findRecent(int limit) {
    return find("", Sort.by("loanDate").descending()).page(0, limit).list();
  }

  /**
   * Finds all active loans (not returned).
   *
   * @return list of active loans
   */
  public List<Loan> findActive() {
    return list("returnDate IS NULL");
  }

  /**
   * Finds all overdue loans.
   *
   * @return list of overdue loans
   */
  public List<Loan> findOverdue() {
    return list("returnDate IS NULL AND dueDate < ?1", LocalDate.now());
  }

  /**
   * Finds loans due within specified days.
   *
   * @param days number of days ahead to check
   * @return list of loans due soon
   */
  public List<Loan> findDueSoon(int days) {
    LocalDate dueDateLimit = LocalDate.now().plusDays(days);
    return list(
        "returnDate IS NULL AND dueDate <= ?1 AND dueDate >= ?2", dueDateLimit, LocalDate.now());
  }

  /**
   * Finds active loans for a specific user.
   *
   * @param userId the user ID
   * @return list of active loans for the user
   */
  public List<Loan> findActiveByUser(Long userId) {
    return list("user.id = ?1 AND returnDate IS NULL", userId);
  }

  /**
   * Finds active loans for a specific book copy.
   *
   * @param bookCopyId the book copy ID
   * @return list of active loans for the book copy
   */
  public List<Loan> findActiveByBookCopy(Long bookCopyId) {
    return list("bookCopy.id = ?1 AND returnDate IS NULL", bookCopyId);
  }

  /**
   * Finds all loans for a specific book copy.
   *
   * @param bookCopyId the book copy ID
   * @return list of all loans for the book copy
   */
  public List<Loan> findByBookCopy(Long bookCopyId) {
    return list("bookCopy.id", bookCopyId);
  }

  /**
   * Finds active loans for a specific book (any copy).
   *
   * @param bookId the book ID
   * @return list of active loans for any copy of the book
   */
  public List<Loan> findActiveByBook(Long bookId) {
    return list("bookCopy.book.id = ?1 AND returnDate IS NULL", bookId);
  }

  /**
   * Finds all loans for a specific book (any copy).
   *
   * @param bookId the book ID
   * @return list of all loans for any copy of the book
   */
  public List<Loan> findByBook(Long bookId) {
    return list("bookCopy.book.id", bookId);
  }

  /**
   * Finds loans returned within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of loans returned in the date range
   */
  public List<Loan> findReturnedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return list("returnDate >= ?1 AND returnDate <= ?2", startDate, endDate);
  }

  /**
   * Finds loans created within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of loans created in the date range
   */
  public List<Loan> findLoanedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return list("loanDate >= ?1 AND loanDate <= ?2", startDate, endDate);
  }

  /**
   * Finds loans with outstanding fines.
   *
   * @return list of loans with unpaid fines
   */
  public List<Loan> findWithOutstandingFines() {
    return list("fineAmount > 0");
  }

  /**
   * Gets the current loan for a specific book copy (if any).
   *
   * @param bookCopyId the book copy ID
   * @return the current loan for the book copy if exists
   */
  public Optional<Loan> getCurrentLoanForBookCopy(Long bookCopyId) {
    return find("bookCopy.id = ?1 AND returnDate IS NULL", bookCopyId).firstResultOptional();
  }

  /**
   * Gets the current loan for a specific book (any copy).
   *
   * @param bookId the book ID
   * @return the current loan for any copy of the book if exists
   */
  public Optional<Loan> getCurrentLoanForBook(Long bookId) {
    return find("bookCopy.book.id = ?1 AND returnDate IS NULL", bookId).firstResultOptional();
  }

  /**
   * Checks if a user has an active loan for a specific book copy.
   *
   * @param userId the user ID
   * @param bookCopyId the book copy ID
   * @return true if user has an active loan for the book copy
   */
  public boolean hasActiveLoanForBookCopy(Long userId, Long bookCopyId) {
    return count("user.id = ?1 AND bookCopy.id = ?2 AND returnDate IS NULL", userId, bookCopyId)
        > 0;
  }

  /**
   * Checks if a user has an active loan for a specific book (any copy).
   *
   * @param userId the user ID
   * @param bookId the book ID
   * @return true if user has an active loan for any copy of the book
   */
  public boolean hasActiveLoanForBook(Long userId, Long bookId) {
    return count("user.id = ?1 AND bookCopy.book.id = ?2 AND returnDate IS NULL", userId, bookId)
        > 0;
  }

  /**
   * Counts active loans for a user.
   *
   * @param userId the user ID
   * @return number of active loans
   */
  public long countActiveByUser(Long userId) {
    return count("user.id = ?1 AND returnDate IS NULL", userId);
  }

  /**
   * Counts overdue loans for a user.
   *
   * @param userId the user ID
   * @return number of overdue loans
   */
  public long countOverdueByUser(Long userId) {
    return count("user.id = ?1 AND returnDate IS NULL AND dueDate < ?2", userId, LocalDate.now());
  }

  /**
   * Gets the most borrowed books.
   *
   * @param limit the maximum number of results
   * @return list of most borrowed books with loan count
   */
  @SuppressWarnings("unchecked")
  public List<Object[]> getMostBorrowedBooks(int limit) {
    return em.createQuery(
            "SELECT l.bookCopy.book, COUNT(l) as loanCount FROM Loan l GROUP BY l.bookCopy.book ORDER BY COUNT(l) DESC")
        .setMaxResults(limit)
        .getResultList();
  }

  /**
   * Gets loan statistics.
   *
   * @return loan statistics
   */
  public LoanStats getLoanStats() {
    long totalLoans = count();
    long activeLoans = count("returnDate IS NULL");
    long overdueLoans = count("returnDate IS NULL AND dueDate < ?1", LocalDate.now());
    long returnedLoans = count("returnDate IS NOT NULL");

    return new LoanStats(totalLoans, activeLoans, overdueLoans, returnedLoans);
  }

  /**
   * Gets loan statistics for a specific library.
   *
   * @param libraryId the library ID
   * @return loan statistics for the library
   */
  public LoanStats getLoanStatsByLibrary(Long libraryId) {
    long totalLoans = count("user.library.id = ?1", libraryId);
    long activeLoans = count("user.library.id = ?1 AND returnDate IS NULL", libraryId);
    long overdueLoans =
        count(
            "user.library.id = ?1 AND returnDate IS NULL AND dueDate < ?2",
            libraryId,
            LocalDate.now());
    long returnedLoans = count("user.library.id = ?1 AND returnDate IS NOT NULL", libraryId);

    return new LoanStats(totalLoans, activeLoans, overdueLoans, returnedLoans);
  }

  /**
   * Gets users with the most active loans.
   *
   * @param limit the maximum number of results
   * @return list of users with most active loans
   */
  @SuppressWarnings("unchecked")
  public List<Object[]> getUsersWithMostActiveLoans(int limit) {
    return em.createQuery(
            "SELECT l.user, COUNT(l) as activeLoanCount FROM Loan l WHERE l.returnDate IS NULL GROUP BY l.user ORDER BY COUNT(l) DESC")
        .setMaxResults(limit)
        .getResultList();
  }

  /** Loan statistics DTO. */
  public static class LoanStats {
    public final long totalLoans;
    public final long activeLoans;
    public final long overdueLoans;
    public final long returnedLoans;

    public LoanStats(long totalLoans, long activeLoans, long overdueLoans, long returnedLoans) {
      this.totalLoans = totalLoans;
      this.activeLoans = activeLoans;
      this.overdueLoans = overdueLoans;
      this.returnedLoans = returnedLoans;
    }
  }
}
