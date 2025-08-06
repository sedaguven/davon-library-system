package com.davonlibrary.service;

import com.davonlibrary.entity.Fine;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.repository.FineRepository;
import com.davonlibrary.repository.LoanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Service for managing fine operations and business logic. */
@ApplicationScoped
public class FineService {

  @Inject FineRepository fineRepository;

  @Inject LoanRepository loanRepository;

  /**
   * Creates a fine for an overdue loan.
   *
   * @param loan the overdue loan
   * @return the created fine
   */
  @Transactional
  public Fine createFineForLoan(Loan loan) {
    if (!loan.isOverdue()) {
      throw new IllegalArgumentException("Cannot create fine for non-overdue loan");
    }

    // Check if fine already exists for this loan
    Optional<Fine> existingFine = fineRepository.findCurrentByLoan(loan.id);
    if (existingFine.isPresent()) {
      return existingFine.get();
    }

    Fine fine = new Fine(loan);
    fine.persist();
    return fine;
  }

  /**
   * Creates a fine with custom daily rate.
   *
   * @param loan the overdue loan
   * @param dailyRate the custom daily rate
   * @return the created fine
   */
  @Transactional
  public Fine createFineForLoan(Loan loan, BigDecimal dailyRate) {
    if (!loan.isOverdue()) {
      throw new IllegalArgumentException("Cannot create fine for non-overdue loan");
    }

    // Check if fine already exists for this loan
    Optional<Fine> existingFine = fineRepository.findCurrentByLoan(loan.id);
    if (existingFine.isPresent()) {
      return existingFine.get();
    }

    Fine fine = new Fine(loan, dailyRate);
    fine.persist();
    return fine;
  }

  /**
   * Processes payment for a fine.
   *
   * @param fineId the fine ID
   * @param paymentAmount the payment amount
   * @param paymentMethod the payment method
   * @param transactionId the transaction ID
   * @return true if payment was processed successfully
   */
  @Transactional
  public boolean processPayment(
      Long fineId, BigDecimal paymentAmount, String paymentMethod, String transactionId) {
    Fine fine = Fine.findById(fineId);
    if (fine == null) {
      return false;
    }

    if (paymentAmount.compareTo(fine.amount) >= 0) {
      // Full payment
      fine.markAsPaid(paymentMethod, transactionId);
    } else {
      // Partial payment
      return fine.processPartialPayment(paymentAmount, paymentMethod, transactionId);
    }

    return true;
  }

  /**
   * Waives a fine.
   *
   * @param fineId the fine ID
   * @param reason the waiver reason
   * @param waivedBy who waived the fine
   * @return true if waiver was successful
   */
  @Transactional
  public boolean waiveFine(Long fineId, String reason, String waivedBy) {
    Fine fine = Fine.findById(fineId);
    if (fine == null || !fine.canBeWaived()) {
      return false;
    }

    fine.waive(reason, waivedBy);
    return true;
  }

  /**
   * Applies a discount to a fine.
   *
   * @param fineId the fine ID
   * @param discountPercentage the discount percentage (0-100)
   * @return true if discount was applied successfully
   */
  @Transactional
  public boolean applyDiscount(Long fineId, int discountPercentage) {
    Fine fine = Fine.findById(fineId);
    if (fine == null || fine.isPaid) {
      return false;
    }

    fine.applyDiscount(discountPercentage);
    return true;
  }

  /**
   * Recalculates all fines that need updating.
   *
   * @return number of fines recalculated
   */
  @Transactional
  public int recalculateAllFines() {
    List<Fine> finesToRecalculate = fineRepository.findNeedingRecalculation();
    int recalculatedCount = 0;

    for (Fine fine : finesToRecalculate) {
      BigDecimal newAmount = fine.recalculateAmount();
      if (newAmount.compareTo(fine.amount) != 0) {
        recalculatedCount++;
      }
    }

    return recalculatedCount;
  }

  /**
   * Gets all active fines for a user.
   *
   * @param userId the user ID
   * @return list of active fines
   */
  public List<Fine> getActiveFinesForUser(Long userId) {
    return fineRepository.findActiveByUser(userId);
  }

  /**
   * Gets the total outstanding fine amount for a user.
   *
   * @param userId the user ID
   * @return the total outstanding amount
   */
  public BigDecimal getTotalOutstandingAmountForUser(Long userId) {
    return fineRepository.getTotalOutstandingAmountByUser(userId);
  }

  /**
   * Checks if a user has outstanding fines.
   *
   * @param userId the user ID
   * @return true if user has outstanding fines
   */
  public boolean hasOutstandingFines(Long userId) {
    return fineRepository.hasOutstandingFines(userId);
  }

  /**
   * Gets fine statistics for a user.
   *
   * @param userId the user ID
   * @return array with [total fines, paid fines, waived fines, outstanding amount]
   */
  public Object[] getFineStatisticsForUser(Long userId) {
    return fineRepository.getFineStatisticsByUser(userId);
  }

  /**
   * Gets all overdue fines (unpaid for more than 30 days).
   *
   * @return list of overdue fines
   */
  public List<Fine> getOverdueFines() {
    return fineRepository.findOverdue();
  }

  /**
   * Gets total fine collection for a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the total amount collected
   */
  public BigDecimal getTotalCollectionBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return fineRepository.getTotalCollectedBetween(startDate, endDate);
  }

  /**
   * Gets total waived amount for a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return the total amount waived
   */
  public BigDecimal getTotalWaivedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return fineRepository.getTotalWaivedBetween(startDate, endDate);
  }

  /**
   * Creates fines for all overdue loans.
   *
   * @return number of fines created
   */
  @Transactional
  public int createFinesForOverdueLoans() {
    List<Loan> overdueLoans = loanRepository.findOverdue();
    int finesCreated = 0;

    for (Loan loan : overdueLoans) {
      try {
        createFineForLoan(loan);
        finesCreated++;
      } catch (Exception e) {
        // Log error but continue processing other loans
        System.err.println("Error creating fine for loan " + loan.id + ": " + e.getMessage());
      }
    }

    return finesCreated;
  }

  /**
   * Gets the progressive fine amount for a loan.
   *
   * @param loanId the loan ID
   * @return the progressive fine amount
   */
  public BigDecimal getProgressiveFineAmount(Long loanId) {
    Optional<Fine> fine = fineRepository.findCurrentByLoan(loanId);
    if (fine.isPresent()) {
      return fine.get().getProgressiveFineAmount();
    }
    return BigDecimal.ZERO;
  }

  /**
   * Checks if a fine can be paid.
   *
   * @param fineId the fine ID
   * @return true if the fine can be paid
   */
  public boolean canPayFine(Long fineId) {
    Fine fine = Fine.findById(fineId);
    return fine != null && fine.canBePaid();
  }

  /**
   * Checks if a fine can be waived.
   *
   * @param fineId the fine ID
   * @return true if the fine can be waived
   */
  public boolean canWaiveFine(Long fineId) {
    Fine fine = Fine.findById(fineId);
    return fine != null && fine.canBeWaived();
  }

  /**
   * Gets fine summary for a user.
   *
   * @param userId the user ID
   * @return formatted fine summary
   */
  public String getFineSummaryForUser(Long userId) {
    List<Fine> activeFines = getActiveFinesForUser(userId);
    BigDecimal totalOutstanding = getTotalOutstandingAmountForUser(userId);

    if (activeFines.isEmpty()) {
      return "No outstanding fines";
    }

    return String.format(
        "User has %d active fines with total outstanding amount of $%.2f",
        activeFines.size(), totalOutstanding);
  }
}
