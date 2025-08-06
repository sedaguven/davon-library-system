package com.davonlibrary.dao;

import com.davonlibrary.entity.Loan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Loan entity operations. Provides a clean abstraction layer over database
 * operations.
 */
@ApplicationScoped
public class LoanDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoanDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a loan by ID.
   *
   * @param id the loan ID
   * @return Optional containing the loan if found
   */
  @Transactional
  public Optional<Loan> findById(Long id) {
    try {
      Loan loan = entityManager.find(Loan.class, id);
      return Optional.ofNullable(loan);
    } catch (Exception e) {
      LOGGER.error("Error finding loan by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find all loans for a user.
   *
   * @param userId the user ID
   * @return list of loans for the user
   */
  @Transactional
  public List<Loan> findByUserId(Long userId) {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.user.id = :userId ORDER BY l.loanDate DESC",
              Loan.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding loans by user ID: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Find active loans for a user (not returned).
   *
   * @param userId the user ID
   * @return list of active loans
   */
  @Transactional
  public List<Loan> findActiveByUserId(Long userId) {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.user.id = :userId "
                  + "AND l.returnDate IS NULL AND l.status.statusName = 'ACTIVE' "
                  + "ORDER BY l.dueDate",
              Loan.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding active loans by user ID: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Find overdue loans.
   *
   * @return list of overdue loans
   */
  @Transactional
  public List<Loan> findOverdue() {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.dueDate < CURRENT_DATE "
                  + "AND l.returnDate IS NULL AND l.status.statusName = 'ACTIVE' "
                  + "ORDER BY l.dueDate",
              Loan.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding overdue loans", e);
      return List.of();
    }
  }

  /**
   * Find loans due soon (within specified days).
   *
   * @param days number of days to look ahead
   * @return list of loans due soon
   */
  @Transactional
  public List<Loan> findDueSoon(int days) {
    try {
      LocalDate dueDate = LocalDate.now().plusDays(days);
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.dueDate <= :dueDate "
                  + "AND l.returnDate IS NULL AND l.status.statusName = 'ACTIVE' "
                  + "ORDER BY l.dueDate",
              Loan.class);
      query.setParameter("dueDate", dueDate);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding loans due soon", e);
      return List.of();
    }
  }

  /**
   * Find loans by book copy.
   *
   * @param bookCopyId the book copy ID
   * @return list of loans for the book copy
   */
  @Transactional
  public List<Loan> findByBookCopyId(Long bookCopyId) {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.bookCopy.id = :bookCopyId "
                  + "ORDER BY l.loanDate DESC",
              Loan.class);
      query.setParameter("bookCopyId", bookCopyId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding loans by book copy ID: {}", bookCopyId, e);
      return List.of();
    }
  }

  /**
   * Find loans by status.
   *
   * @param statusName the status name
   * @return list of loans with the specified status
   */
  @Transactional
  public List<Loan> findByStatus(String statusName) {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.status.statusName = :statusName "
                  + "ORDER BY l.loanDate DESC",
              Loan.class);
      query.setParameter("statusName", statusName);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding loans by status: {}", statusName, e);
      return List.of();
    }
  }

  /**
   * Find loans within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of loans within the date range
   */
  @Transactional
  public List<Loan> findByDateRange(LocalDate startDate, LocalDate endDate) {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.loanDate >= :startDate "
                  + "AND l.loanDate <= :endDate ORDER BY l.loanDate DESC",
              Loan.class);
      query.setParameter("startDate", startDate);
      query.setParameter("endDate", endDate);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding loans by date range: {} to {}", startDate, endDate, e);
      return List.of();
    }
  }

  /**
   * Save a new loan.
   *
   * @param loan the loan to save
   * @return the saved loan with generated ID
   */
  @Transactional
  public Loan save(Loan loan) {
    try {
      entityManager.persist(loan);
      entityManager.flush();
      LOGGER.info("Loan saved successfully: User={}, BookCopy={}", loan.user.id, loan.bookCopy.id);
      return loan;
    } catch (Exception e) {
      LOGGER.error("Error saving loan: User={}, BookCopy={}", loan.user.id, loan.bookCopy.id, e);
      throw new RuntimeException("Failed to save loan", e);
    }
  }

  /**
   * Update an existing loan.
   *
   * @param loan the loan to update
   * @return the updated loan
   */
  @Transactional
  public Loan update(Loan loan) {
    try {
      Loan updatedLoan = entityManager.merge(loan);
      LOGGER.info("Loan updated successfully: ID={}", loan.id);
      return updatedLoan;
    } catch (Exception e) {
      LOGGER.error("Error updating loan: ID={}", loan.id, e);
      throw new RuntimeException("Failed to update loan", e);
    }
  }

  /**
   * Delete a loan by ID.
   *
   * @param id the loan ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      Loan loan = entityManager.find(Loan.class, id);
      if (loan != null) {
        entityManager.remove(loan);
        LOGGER.info("Loan deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting loan with ID: {}", id, e);
      throw new RuntimeException("Failed to delete loan", e);
    }
  }

  /**
   * Find all loans.
   *
   * @return list of all loans
   */
  @Transactional
  public List<Loan> findAll() {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery("SELECT l FROM Loan l ORDER BY l.loanDate DESC", Loan.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all loans", e);
      return List.of();
    }
  }

  /**
   * Count total number of loans.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(l) FROM Loan l", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting loans", e);
      return 0;
    }
  }

  /**
   * Count active loans for a user.
   *
   * @param userId the user ID
   * @return number of active loans
   */
  @Transactional
  public long countActiveByUserId(Long userId) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId "
                  + "AND l.returnDate IS NULL AND l.status.statusName = 'ACTIVE'",
              Long.class);
      query.setParameter("userId", userId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting active loans for user: {}", userId, e);
      return 0;
    }
  }

  /**
   * Get loan statistics.
   *
   * @return array with [total loans, active loans, overdue loans, returned loans]
   */
  @Transactional
  public Object[] getLoanStatistics() {
    try {
      // Total loans
      TypedQuery<Long> totalQuery =
          entityManager.createQuery("SELECT COUNT(l) FROM Loan l", Long.class);
      long totalLoans = totalQuery.getSingleResult();

      // Active loans
      TypedQuery<Long> activeQuery =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.returnDate IS NULL "
                  + "AND l.status.statusName = 'ACTIVE'",
              Long.class);
      long activeLoans = activeQuery.getSingleResult();

      // Overdue loans
      TypedQuery<Long> overdueQuery =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.dueDate < CURRENT_DATE "
                  + "AND l.returnDate IS NULL AND l.status.statusName = 'ACTIVE'",
              Long.class);
      long overdueLoans = overdueQuery.getSingleResult();

      // Returned loans
      TypedQuery<Long> returnedQuery =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.returnDate IS NOT NULL", Long.class);
      long returnedLoans = returnedQuery.getSingleResult();

      return new Object[] {totalLoans, activeLoans, overdueLoans, returnedLoans};
    } catch (Exception e) {
      LOGGER.error("Error getting loan statistics", e);
      return new Object[] {0L, 0L, 0L, 0L};
    }
  }

  /**
   * Find loans that can be extended.
   *
   * @param maxExtensions maximum allowed extensions
   * @return list of loans that can be extended
   */
  @Transactional
  public List<Loan> findExtendableLoans(int maxExtensions) {
    try {
      TypedQuery<Loan> query =
          entityManager.createQuery(
              "SELECT l FROM Loan l WHERE l.returnDate IS NULL "
                  + "AND l.status.statusName = 'ACTIVE' "
                  + "AND l.extensionsCount < :maxExtensions "
                  + "ORDER BY l.dueDate",
              Loan.class);
      query.setParameter("maxExtensions", maxExtensions);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding extendable loans", e);
      return List.of();
    }
  }
}
