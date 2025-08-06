package com.davonlibrary.dao;

import com.davonlibrary.entity.Fine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Fine entity operations. Provides a clean abstraction layer over database
 * operations.
 */
@ApplicationScoped
public class FineDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(FineDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a fine by ID.
   *
   * @param id the fine ID
   * @return Optional containing the fine if found
   */
  @Transactional
  public Optional<Fine> findById(Long id) {
    try {
      Fine fine = entityManager.find(Fine.class, id);
      return Optional.ofNullable(fine);
    } catch (Exception e) {
      LOGGER.error("Error finding fine by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find all fines for a user.
   *
   * @param userId the user ID
   * @return list of fines for the user
   */
  @Transactional
  public List<Fine> findByUserId(Long userId) {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery(
              "SELECT f FROM Fine f WHERE f.user.id = :userId ORDER BY f.issueDate DESC",
              Fine.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding fines by user ID: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Find unpaid fines for a user.
   *
   * @param userId the user ID
   * @return list of unpaid fines
   */
  @Transactional
  public List<Fine> findUnpaidByUserId(Long userId) {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery(
              "SELECT f FROM Fine f WHERE f.user.id = :userId "
                  + "AND f.paymentDate IS NULL ORDER BY f.issueDate DESC",
              Fine.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding unpaid fines by user ID: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Find overdue fines (past due date).
   *
   * @return list of overdue fines
   */
  @Transactional
  public List<Fine> findOverdue() {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery(
              "SELECT f FROM Fine f WHERE f.dueDate < CURRENT_DATE "
                  + "AND f.paymentDate IS NULL ORDER BY f.dueDate",
              Fine.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding overdue fines", e);
      return List.of();
    }
  }

  /**
   * Find fines by status.
   *
   * @param status the fine status
   * @return list of fines with the specified status
   */
  @Transactional
  public List<Fine> findByStatus(String status) {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery(
              "SELECT f FROM Fine f WHERE f.status = :status ORDER BY f.issueDate DESC",
              Fine.class);
      query.setParameter("status", status);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding fines by status: {}", status, e);
      return List.of();
    }
  }

  /**
   * Find fines within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of fines within the date range
   */
  @Transactional
  public List<Fine> findByDateRange(LocalDate startDate, LocalDate endDate) {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery(
              "SELECT f FROM Fine f WHERE f.issueDate >= :startDate "
                  + "AND f.issueDate <= :endDate ORDER BY f.issueDate DESC",
              Fine.class);
      query.setParameter("startDate", startDate);
      query.setParameter("endDate", endDate);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding fines by date range: {} to {}", startDate, endDate, e);
      return List.of();
    }
  }

  /**
   * Find fines above a certain amount.
   *
   * @param amount the minimum amount
   * @return list of fines above the amount
   */
  @Transactional
  public List<Fine> findByAmountGreaterThan(BigDecimal amount) {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery(
              "SELECT f FROM Fine f WHERE f.amount > :amount ORDER BY f.amount DESC", Fine.class);
      query.setParameter("amount", amount);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding fines above amount: {}", amount, e);
      return List.of();
    }
  }

  /**
   * Save a new fine.
   *
   * @param fine the fine to save
   * @return the saved fine with generated ID
   */
  @Transactional
  public Fine save(Fine fine) {
    try {
      entityManager.persist(fine);
      entityManager.flush();
      LOGGER.info("Fine saved successfully: User={}, Amount={}", fine.user.id, fine.amount);
      return fine;
    } catch (Exception e) {
      LOGGER.error("Error saving fine: User={}, Amount={}", fine.user.id, fine.amount, e);
      throw new RuntimeException("Failed to save fine", e);
    }
  }

  /**
   * Update an existing fine.
   *
   * @param fine the fine to update
   * @return the updated fine
   */
  @Transactional
  public Fine update(Fine fine) {
    try {
      Fine updatedFine = entityManager.merge(fine);
      LOGGER.info("Fine updated successfully: ID={}", fine.id);
      return updatedFine;
    } catch (Exception e) {
      LOGGER.error("Error updating fine: ID={}", fine.id, e);
      throw new RuntimeException("Failed to update fine", e);
    }
  }

  /**
   * Delete a fine by ID.
   *
   * @param id the fine ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      Fine fine = entityManager.find(Fine.class, id);
      if (fine != null) {
        entityManager.remove(fine);
        LOGGER.info("Fine deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting fine with ID: {}", id, e);
      throw new RuntimeException("Failed to delete fine", e);
    }
  }

  /**
   * Find all fines.
   *
   * @return list of all fines
   */
  @Transactional
  public List<Fine> findAll() {
    try {
      TypedQuery<Fine> query =
          entityManager.createQuery("SELECT f FROM Fine f ORDER BY f.issueDate DESC", Fine.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all fines", e);
      return List.of();
    }
  }

  /**
   * Count total number of fines.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(f) FROM Fine f", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting fines", e);
      return 0;
    }
  }

  /**
   * Count unpaid fines for a user.
   *
   * @param userId the user ID
   * @return number of unpaid fines
   */
  @Transactional
  public long countUnpaidByUserId(Long userId) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(f) FROM Fine f WHERE f.user.id = :userId "
                  + "AND f.paymentDate IS NULL",
              Long.class);
      query.setParameter("userId", userId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting unpaid fines for user: {}", userId, e);
      return 0;
    }
  }

  /**
   * Get fine statistics.
   *
   * @return array with [total fines, unpaid fines, paid fines, total amount]
   */
  @Transactional
  public Object[] getFineStatistics() {
    try {
      // Total fines
      TypedQuery<Long> totalQuery =
          entityManager.createQuery("SELECT COUNT(f) FROM Fine f", Long.class);
      long totalFines = totalQuery.getSingleResult();

      // Unpaid fines
      TypedQuery<Long> unpaidQuery =
          entityManager.createQuery(
              "SELECT COUNT(f) FROM Fine f WHERE f.paymentDate IS NULL", Long.class);
      long unpaidFines = unpaidQuery.getSingleResult();

      // Paid fines
      TypedQuery<Long> paidQuery =
          entityManager.createQuery(
              "SELECT COUNT(f) FROM Fine f WHERE f.paymentDate IS NOT NULL", Long.class);
      long paidFines = paidQuery.getSingleResult();

      // Total amount
      TypedQuery<BigDecimal> totalAmountQuery =
          entityManager.createQuery(
              "SELECT COALESCE(SUM(f.amount), 0) FROM Fine f", BigDecimal.class);
      BigDecimal totalAmount = totalAmountQuery.getSingleResult();

      return new Object[] {totalFines, unpaidFines, paidFines, totalAmount};
    } catch (Exception e) {
      LOGGER.error("Error getting fine statistics", e);
      return new Object[] {0L, 0L, 0L, BigDecimal.ZERO};
    }
  }

  /**
   * Calculate total outstanding amount for a user.
   *
   * @param userId the user ID
   * @return total outstanding amount
   */
  @Transactional
  public BigDecimal getTotalOutstandingAmount(Long userId) {
    try {
      TypedQuery<BigDecimal> query =
          entityManager.createQuery(
              "SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.user.id = :userId "
                  + "AND f.paymentDate IS NULL",
              BigDecimal.class);
      query.setParameter("userId", userId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error calculating total outstanding amount for user: {}", userId, e);
      return BigDecimal.ZERO;
    }
  }
}
