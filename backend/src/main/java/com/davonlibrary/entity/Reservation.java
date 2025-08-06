package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Reservation entity for tracking book reservations when books are not immediately available. */
@Entity
@Table(name = "reservations")
public class Reservation extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @NotNull(message = "User is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  public User user;

  @NotNull(message = "Book is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  public Book book;

  @NotNull(message = "Reservation date is required")
  @Column(name = "reservation_date", nullable = false)
  public LocalDateTime reservationDate = LocalDateTime.now();

  @NotNull(message = "Expiry date is required")
  @Column(name = "expiry_date", nullable = false)
  public LocalDate expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ReservationStatus status = ReservationStatus.ACTIVE;

  @Column(name = "queue_position")
  public Integer queuePosition;

  @Column(name = "notification_sent_date")
  public LocalDateTime notificationSentDate;

  @Size(max = 500, message = "Notes must not exceed 500 characters")
  @Column(name = "notes", length = 500)
  public String notes;

  /** Reservation status enumeration. */
  public enum ReservationStatus {
    ACTIVE,
    PENDING,
    FULFILLED,
    CANCELLED,
    EXPIRED
  }

  /** Default constructor for JPA. */
  public Reservation() {}

  /**
   * Constructor with essential fields.
   *
   * @param user the user making the reservation
   * @param book the book being reserved
   * @param expiryDate when the reservation expires
   */
  public Reservation(User user, Book book, LocalDate expiryDate) {
    this.user = user;
    this.book = book;
    this.expiryDate = expiryDate;
  }

  /**
   * Constructor with queue position.
   *
   * @param user the user making the reservation
   * @param book the book being reserved
   * @param expiryDate when the reservation expires
   * @param queuePosition position in the reservation queue
   */
  public Reservation(User user, Book book, LocalDate expiryDate, Integer queuePosition) {
    this.user = user;
    this.book = book;
    this.expiryDate = expiryDate;
    this.queuePosition = queuePosition;
  }

  /**
   * Checks if the reservation is currently active.
   *
   * @return true if the reservation is active and not expired
   */
  public boolean isActive() {
    return status == ReservationStatus.ACTIVE && !isExpired();
  }

  /**
   * Checks if the reservation has expired.
   *
   * @return true if the current date is after the expiry date
   */
  public boolean isExpired() {
    return LocalDate.now().isAfter(expiryDate);
  }

  /**
   * Marks the reservation as fulfilled when the book becomes available.
   *
   * @param notificationDate when the user was notified
   */
  public void fulfill(LocalDateTime notificationDate) {
    this.status = ReservationStatus.FULFILLED;
    this.notificationSentDate = notificationDate;
  }

  /**
   * Cancels the reservation.
   *
   * @param reason reason for cancellation
   */
  public void cancel(String reason) {
    this.status = ReservationStatus.CANCELLED;
    this.notes = reason;
  }

  /** Marks the reservation as expired. */
  public void expire() {
    this.status = ReservationStatus.EXPIRED;
  }

  /**
   * Extends the expiry date of the reservation.
   *
   * @param additionalDays number of days to extend
   */
  public void extendExpiry(int additionalDays) {
    if (isActive()) {
      this.expiryDate = this.expiryDate.plusDays(additionalDays);
    }
  }

  /**
   * Gets the number of days until expiry.
   *
   * @return days until expiry, negative if already expired
   */
  public long getDaysUntilExpiry() {
    return LocalDate.now().until(expiryDate).getDays();
  }

  /**
   * Gets the estimated wait time based on queue position.
   *
   * @param averageLoanDays average loan duration in days
   * @return estimated days to wait
   */
  public int getEstimatedWaitDays(int averageLoanDays) {
    if (queuePosition == null || queuePosition <= 1) {
      return 0;
    }
    return (queuePosition - 1) * averageLoanDays;
  }

  @Override
  public String toString() {
    return "Reservation{"
        + "id="
        + id
        + ", user="
        + (user != null ? user.getFullName() : "null")
        + ", book="
        + (book != null ? book.title : "null")
        + ", reservationDate="
        + reservationDate
        + ", expiryDate="
        + expiryDate
        + ", status="
        + status
        + ", queuePosition="
        + queuePosition
        + '}';
  }
}
