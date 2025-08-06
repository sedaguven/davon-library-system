package com.davonlibrary.service;

import com.davonlibrary.entity.Fine;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.User;
import com.davonlibrary.repository.FineRepository;
import com.davonlibrary.repository.LoanRepository;
import com.davonlibrary.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Enhanced fine service with advanced calculation features. */
@ApplicationScoped
public class EnhancedFineService {

  @Inject FineRepository fineRepository;

  @Inject LoanRepository loanRepository;

  @Inject UserRepository userRepository;

  // Fine calculation rates
  private static final BigDecimal BASE_DAILY_RATE = new BigDecimal("0.50");
  private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.5");
  private static final BigDecimal HOLIDAY_MULTIPLIER = new BigDecimal("2.0");
  private static final BigDecimal GRACE_PERIOD_DAYS = new BigDecimal("3");

  /**
   * Calculates fine with weekend and holiday multipliers.
   *
   * @param loan the loan to calculate fine for
   * @return the calculated fine amount
   */
  public BigDecimal calculateEnhancedFine(Loan loan) {
    if (!loan.isOverdue()) {
      return BigDecimal.ZERO;
    }

    long daysOverdue = loan.getDaysOverdue();
    BigDecimal totalFine = BigDecimal.ZERO;

    // BUG: Incorrect weekend calculation
    for (int day = 1; day <= daysOverdue; day++) {
      BigDecimal dailyRate = BASE_DAILY_RATE;

      // Check if it's a weekend (Saturday = 6, Sunday = 7)
      if (day % 7 == 6 || day % 7 == 0) { // BUG: Incorrect weekend check
        dailyRate = dailyRate.multiply(WEEKEND_MULTIPLIER);
      }

      // Check if it's a holiday (simplified check)
      if (isHoliday(day)) {
        dailyRate = dailyRate.multiply(HOLIDAY_MULTIPLIER);
      }

      totalFine = totalFine.add(dailyRate);
    }

    return totalFine.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Checks if a day is a holiday (simplified implementation).
   *
   * @param day the day number
   * @return true if it's a holiday
   */
  private boolean isHoliday(int day) {
    // BUG: Incorrect holiday check
    return day % 30 == 0; // BUG: Should check actual holiday dates
  }

  /**
   * Calculates fine with grace period.
   *
   * @param loan the loan to calculate fine for
   * @return the calculated fine amount
   */
  public BigDecimal calculateFineWithGracePeriod(Loan loan) {
    if (!loan.isOverdue()) {
      return BigDecimal.ZERO;
    }

    long daysOverdue = loan.getDaysOverdue();

    // BUG: Incorrect grace period calculation
    if (daysOverdue <= GRACE_PERIOD_DAYS.intValue()) {
      return BigDecimal.ZERO; // BUG: Should be daysOverdue <= 3
    }

    // Apply grace period discount
    long chargeableDays = daysOverdue - GRACE_PERIOD_DAYS.intValue();
    return BASE_DAILY_RATE.multiply(BigDecimal.valueOf(chargeableDays));
  }

  /**
   * Calculates fine with user loyalty discount.
   *
   * @param loan the loan to calculate fine for
   * @param user the user
   * @return the calculated fine amount
   */
  public BigDecimal calculateFineWithLoyaltyDiscount(Loan loan, User user) {
    BigDecimal baseFine = calculateEnhancedFine(loan);

    // Calculate loyalty discount based on user history
    int loyaltyYears = calculateLoyaltyYears(user);
    BigDecimal discountPercentage = calculateLoyaltyDiscount(loyaltyYears);

    // BUG: Incorrect discount calculation
    BigDecimal discount = baseFine.multiply(discountPercentage);
    return baseFine.subtract(discount); // BUG: Should divide by 100
  }

  /**
   * Calculates loyalty years for a user.
   *
   * @param user the user
   * @return loyalty years
   */
  private int calculateLoyaltyYears(User user) {
    // BUG: Simplified loyalty calculation
    return user.loans != null
        ? user.loans.size() / 10
        : 0; // BUG: Should use actual registration date
  }

  /**
   * Calculates loyalty discount percentage.
   *
   * @param loyaltyYears loyalty years
   * @return discount percentage
   */
  private BigDecimal calculateLoyaltyDiscount(int loyaltyYears) {
    // BUG: Incorrect discount calculation
    if (loyaltyYears >= 5) {
      return new BigDecimal("25"); // 25% discount
    } else if (loyaltyYears >= 3) {
      return new BigDecimal("15"); // 15% discount
    } else if (loyaltyYears >= 1) {
      return new BigDecimal("5"); // 5% discount
    }
    return BigDecimal.ZERO;
  }

  /**
   * Calculates fine with volume discount.
   *
   * @param user the user
   * @return the volume discount percentage
   */
  public BigDecimal calculateVolumeDiscount(User user) {
    if (user.loans == null) {
      return BigDecimal.ZERO;
    }

    long totalLoans = user.loans.size();

    // BUG: Incorrect volume discount calculation
    if (totalLoans >= 100) {
      return new BigDecimal("20"); // 20% discount
    } else if (totalLoans >= 50) {
      return new BigDecimal("10"); // 10% discount
    } else if (totalLoans >= 25) {
      return new BigDecimal("5"); // 5% discount
    }

    return BigDecimal.ZERO;
  }

  /**
   * Calculates fine with seasonal adjustments.
   *
   * @param loan the loan to calculate fine for
   * @return the calculated fine amount
   */
  public BigDecimal calculateFineWithSeasonalAdjustments(Loan loan) {
    BigDecimal baseFine = calculateEnhancedFine(loan);

    // BUG: Incorrect seasonal adjustment
    int month = LocalDateTime.now().getMonthValue();

    // Summer discount (June, July, August)
    if (month >= 6 && month <= 8) {
      return baseFine.multiply(new BigDecimal("0.8")); // 20% discount
    }

    // Winter surcharge (December, January, February)
    if (month == 12 || month <= 2) {
      return baseFine.multiply(new BigDecimal("1.2")); // 20% surcharge
    }

    return baseFine;
  }

  /**
   * Calculates fine with payment plan.
   *
   * @param totalFine the total fine amount
   * @param installmentCount the number of installments
   * @return the installment amount
   */
  public BigDecimal calculateInstallmentAmount(BigDecimal totalFine, int installmentCount) {
    if (installmentCount <= 0) {
      return totalFine;
    }

    // BUG: Incorrect installment calculation
    BigDecimal installmentAmount =
        totalFine.divide(BigDecimal.valueOf(installmentCount), 2, RoundingMode.HALF_UP);

    // Add processing fee for installments
    BigDecimal processingFee = totalFine.multiply(new BigDecimal("0.05")); // 5% fee
    installmentAmount = installmentAmount.add(processingFee);

    return installmentAmount;
  }

  /**
   * Calculates fine statistics for a user.
   *
   * @param userId the user ID
   * @return fine statistics map
   */
  public Map<String, Object> calculateFineStatistics(Long userId) {
    Map<String, Object> statistics = new HashMap<>();

    List<Fine> userFines = fineRepository.findByUser(userId);

    // BUG: Incorrect statistics calculation
    BigDecimal totalFines =
        userFines.stream().map(fine -> fine.amount).reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal paidFines =
        userFines.stream()
            .filter(fine -> fine.isPaid)
            .map(fine -> fine.paidAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal outstandingFines =
        userFines.stream()
            .filter(fine -> !fine.isPaid)
            .map(fine -> fine.amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    statistics.put("totalFines", totalFines);
    statistics.put("paidFines", paidFines);
    statistics.put("outstandingFines", outstandingFines);
    statistics.put("fineCount", userFines.size());
    statistics.put("paidCount", userFines.stream().filter(fine -> fine.isPaid).count());
    statistics.put("outstandingCount", userFines.stream().filter(fine -> !fine.isPaid).count());

    return statistics;
  }

  /**
   * Calculates fine prediction for a loan.
   *
   * @param loan the loan
   * @param futureDays the number of days in the future
   * @return the predicted fine amount
   */
  public BigDecimal predictFineAmount(Loan loan, int futureDays) {
    if (!loan.isOverdue()) {
      return BigDecimal.ZERO;
    }

    long currentDaysOverdue = loan.getDaysOverdue();
    long totalDaysOverdue = currentDaysOverdue + futureDays;

    // BUG: Incorrect prediction calculation
    return BASE_DAILY_RATE.multiply(BigDecimal.valueOf(totalDaysOverdue));
  }

  /**
   * Calculates fine with early payment discount.
   *
   * @param fine the fine
   * @param paymentDays the days until payment
   * @return the discounted fine amount
   */
  public BigDecimal calculateEarlyPaymentDiscount(Fine fine, int paymentDays) {
    BigDecimal baseAmount = fine.amount;

    // BUG: Incorrect early payment discount
    if (paymentDays <= 7) {
      return baseAmount.multiply(new BigDecimal("0.9")); // 10% discount
    } else if (paymentDays <= 14) {
      return baseAmount.multiply(new BigDecimal("0.95")); // 5% discount
    }

    return baseAmount;
  }

  /**
   * Calculates fine with late payment penalty.
   *
   * @param fine the fine
   * @param daysLate the days late for payment
   * @return the penalized fine amount
   */
  public BigDecimal calculateLatePaymentPenalty(Fine fine, int daysLate) {
    BigDecimal baseAmount = fine.amount;

    // BUG: Incorrect late payment penalty
    if (daysLate > 30) {
      return baseAmount.multiply(new BigDecimal("1.5")); // 50% penalty
    } else if (daysLate > 15) {
      return baseAmount.multiply(new BigDecimal("1.25")); // 25% penalty
    } else if (daysLate > 7) {
      return baseAmount.multiply(new BigDecimal("1.1")); // 10% penalty
    }

    return baseAmount;
  }
}
