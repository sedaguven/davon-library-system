package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
public class Fine extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @NotNull(message = "Loan information is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loan_id", nullable = false)
  public Loan loan;

  @NotNull(message = "User is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  public User user;

  @NotNull(message = "Fine amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Fine amount must be positive")
  @Column(name = "amount", nullable = false)
  public BigDecimal amount;

  @NotNull(message = "Fine date is required")
  @Column(name = "fine_date", nullable = false)
  public LocalDate fineDate;

  @NotBlank(message = "Reason for the fine is required")
  @Column(name = "reason", nullable = false)
  public String reason;

  @NotNull(message = "Payment status is required")
  @Column(name = "paid", nullable = false)
  public boolean isPaid = false;

  @Column(name = "payment_date")
  public LocalDate paymentDate;

  @Column(name = "paid_amount", precision = 10, scale = 2)
  public BigDecimal paidAmount = BigDecimal.ZERO;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public FineStatus status = FineStatus.ACTIVE;

  @Column(name = "waiver_reason", length = 500)
  public String waiverReason;

  @Column(name = "waived_by")
  public String waivedBy;

  @Column(name = "waived_date")
  public LocalDateTime waivedDate;

  @Column(name = "payment_method", length = 50)
  public String paymentMethod;

  @Column(name = "transaction_id", length = 100)
  public String transactionId;

  @Column(name = "notes", length = 500)
  public String notes;

  /** Fine status enumeration. */
  public enum FineStatus {
    ACTIVE,
    PAID,
    WAIVED,
    CANCELLED,
    PARTIALLY_PAID
  }

  /** Default constructor for JPA. */
  public Fine() {}

  /**
   * Constructor for creating a new fine.
   *
   * @param loan the associated loan
   * @param amount the fine amount
   * @param reason the reason for the fine
   */
  public Fine(Loan loan, BigDecimal amount, String reason) {
    this.loan = loan;
    this.user = loan.user;
    this.amount = amount;
    this.reason = reason;
    this.fineDate = LocalDate.now();
    this.isPaid = false;
  }

  public Fine(Loan loan) {
    this(loan, BigDecimal.ZERO, "Default reason");
  }

  public Fine(Loan loan, BigDecimal amount) {
    this(loan, amount, "Default reason");
  }

  /**
   * Gets the remaining unpaid amount.
   *
   * @return the remaining amount to be paid
   */
  public BigDecimal getRemainingAmount() {
    return amount.subtract(paidAmount);
  }

  /**
   * Marks the fine as paid.
   *
   * @param paymentMethod the method of payment
   * @param transactionId the transaction ID
   */
  public void markAsPaid(String paymentMethod, String transactionId) {
    this.isPaid = true;
    this.paymentDate = LocalDate.now();
    this.paidAmount = amount;
    this.status = FineStatus.PAID;
    this.paymentMethod = paymentMethod;
    this.transactionId = transactionId;
  }

  public boolean processPartialPayment(
      BigDecimal paymentAmount, String paymentMethod, String transactionId) {
    if (isPaid || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
      return false;
    }

    BigDecimal remainingAmount = getRemainingAmount();
    if (paymentAmount.compareTo(remainingAmount) > 0) {
      return false;
    }

    this.paidAmount = this.paidAmount.add(paymentAmount);
    this.paymentMethod = paymentMethod;
    this.transactionId = transactionId;

    if (this.paidAmount.compareTo(this.amount) >= 0) {
      this.isPaid = true;
      this.paymentDate = LocalDate.now();
      this.status = FineStatus.PAID;
    } else {
      this.status = FineStatus.PARTIALLY_PAID;
    }

    return true;
  }

  /**
   * Waives the fine.
   *
   * @param reason reason for waiver
   * @param waivedBy who waived the fine
   */
  public void waive(String reason, String waivedBy) {
    this.status = FineStatus.WAIVED;
    this.waiverReason = reason;
    this.waivedBy = waivedBy;
    this.waivedDate = LocalDateTime.now();
    this.amount = BigDecimal.ZERO;
  }

  public boolean canBeWaived() {
    return status == FineStatus.ACTIVE && !isPaid;
  }

  public boolean canBePaid() {
    return status == FineStatus.ACTIVE && !isPaid;
  }

  public BigDecimal recalculateAmount() {
    // Implement logic if needed
    return amount;
  }

  public BigDecimal getProgressiveFineAmount() {
    // Implement logic if needed
    return amount;
  }

  public void applyDiscount(int discountPercentage) {
    // Implement logic if needed
  }

  @Override
  public String toString() {
    return "Fine{"
        + "id="
        + id
        + ", amount="
        + amount
        + ", reason='"
        + reason
        + '\''
        + ", paid="
        + isPaid
        + '}';
  }
}
