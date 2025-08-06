package com.davonlibrary.repository;

import com.davonlibrary.entity.Fine;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Repository for Fine entity operations and queries. */
@ApplicationScoped
public class FineRepository implements PanacheRepository<Fine> {

  @Inject EntityManager em;

  /**
   * Finds all active fines (not paid or waived).
   *
   * @return list of active fines
   */
  public List<Fine> findActive() {
    return list("status = ?1", Fine.FineStatus.ACTIVE);
  }

  /**
   * Finds all paid fines.
   *
   * @return list of paid fines
   */
  public List<Fine> findPaid() {
    return list("status = ?1", Fine.FineStatus.PAID);
  }

  /**
   * Finds all waived fines.
   *
   * @return list of waived fines
   */
  public List<Fine> findWaived() {
    return list("status = ?1", Fine.FineStatus.WAIVED);
  }

  /**
   * Finds all overdue fines (unpaid for more than 30 days).
   *
   * @return list of overdue fines
   */
  public List<Fine> findOverdue() {
    LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
    return list("status = ?1 AND createdDate < ?2", Fine.FineStatus.ACTIVE, thirtyDaysAgo);
  }

  /**
   * Finds fines for a specific user.
   *
   * @param userId the user ID
   * @return list of fines for the user
   */
  public List<Fine> findByUser(Long userId) {
    return list("user.id", userId);
  }

  /**
   * Finds active fines for a specific user.
   *
   * @param userId the user ID
   * @return list of active fines for the user
   */
  public List<Fine> findActiveByUser(Long userId) {
    return list("user.id = ?1 AND status = ?2", userId, Fine.FineStatus.ACTIVE);
  }

  /**
   * Finds fines for a specific loan.
   *
   * @param loanId the loan ID
   * @return list of fines for the loan
   */
  public List<Fine> findByLoan(Long loanId) {
    return list("loan.id", loanId);
  }

  /**
   * Finds the current active fine for a specific loan.
   *
   * @param loanId the loan ID
   * @return the current fine if exists
   */
  public Optional<Fine> findCurrentByLoan(Long loanId) {
    return find("loan.id = ?1 AND status = ?2", loanId, Fine.FineStatus.ACTIVE)
        .firstResultOptional();
  }

  /**
   * Finds fines with amounts greater than a specified amount.
   *
   * @param amount the minimum amount
   * @return list of fines with amounts greater than the specified amount
   */
  public List<Fine> findByAmountGreaterThan(BigDecimal amount) {
    return list("amount > ?1", amount);
  }

  /**
   * Finds fines created within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of fines created in the date range
   */
  public List<Fine> findCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return list("createdDate >= ?1 AND createdDate <= ?2", startDate, endDate);
  }

  /**
   * Finds fines paid within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of fines paid in the date range
   */
  public List<Fine> findPaidBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return list("paidDate >= ?1 AND paidDate <= ?2", startDate, endDate);
  }

  /**
   * Finds fines by payment method.
   *
   * @param paymentMethod the payment method
   * @return list of fines with the specified payment method
   */
  public List<Fine> findByPaymentMethod(String paymentMethod) {
    return list("paymentMethod", paymentMethod);
  }

  /**
   * Finds fines by waiver reason.
   *
   * @param waiverReason the waiver reason
   * @return list of fines with the specified waiver reason
   */
  public List<Fine> findByWaiverReason(String waiverReason) {
    return list("waiverReason", waiverReason);
  }

  /**
   * Counts active fines for a user.
   *
   * @param userId the user ID
   * @return number of active fines
   */
  public long countActiveByUser(Long userId) {
    return count("user.id = ?1 AND status = ?2", userId, Fine.FineStatus.ACTIVE);
  }

  /**
   * Counts overdue fines for a user.
   *
   * @param userId the user ID
   * @return number of overdue fines
   */
  public long countOverdueByUser(Long userId) {
    LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
    return count(
        "user.id = ?1 AND status = ?2 AND createdDate < ?3",
        userId,
        Fine.FineStatus.ACTIVE,
        thirtyDaysAgo);
  }

  /**
   * Gets the total outstanding fine amount for a user.
   *
   * @param userId the user ID
   * @return the total outstanding amount
   */
  public BigDecimal getTotalOutstandingAmountByUser(Long userId) {
    List<Fine> activeFines = findActiveByUser(userId);
    return activeFines.stream()
        .map(Fine::getRemainingAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Gets the total fine amount collected in a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the total amount collected
   */
  public BigDecimal getTotalCollectedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    List<Fine> paidFines = findPaidBetween(startDate, endDate);
    return paidFines.stream().map(fine -> fine.paidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Gets the total waived amount in a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the total amount waived
   */
  public BigDecimal getTotalWaivedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    List<Fine> waivedFines =
        list(
            "status = ?1 AND waivedDate >= ?2 AND waivedDate <= ?3",
            Fine.FineStatus.WAIVED,
            startDate,
            endDate);

    return waivedFines.stream().map(fine -> fine.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Finds fines that need to be recalculated (overdue loans).
   *
   * @return list of fines that need recalculation
   */
  public List<Fine> findNeedingRecalculation() {
    return list(
        "SELECT f FROM Fine f JOIN f.loan l WHERE f.status = ?1 AND l.returnDate IS NULL AND l.dueDate < CURRENT_DATE",
        Fine.FineStatus.ACTIVE);
  }

  /**
   * Finds fines by transaction ID.
   *
   * @param transactionId the transaction ID
   * @return the fine with the specified transaction ID if exists
   */
  public Optional<Fine> findByTransactionId(String transactionId) {
    return find("transactionId", transactionId).firstResultOptional();
  }

  /**
   * Checks if a user has any outstanding fines.
   *
   * @param userId the user ID
   * @return true if user has outstanding fines
   */
  public boolean hasOutstandingFines(Long userId) {
    return countActiveByUser(userId) > 0;
  }

  /**
   * Gets the total fine statistics for a user.
   *
   * @param userId the user ID
   * @return array with [total fines, paid fines, waived fines, outstanding amount]
   */
  public Object[] getFineStatisticsByUser(Long userId) {
    long totalFines = count("user.id", userId);
    long paidFines = count("user.id = ?1 AND status = ?2", userId, Fine.FineStatus.PAID);
    long waivedFines = count("user.id = ?1 AND status = ?2", userId, Fine.FineStatus.WAIVED);
    BigDecimal outstandingAmount = getTotalOutstandingAmountByUser(userId);

    return new Object[] {totalFines, paidFines, waivedFines, outstandingAmount};
  }
}
