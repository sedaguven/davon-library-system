package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/** ReservationQueue entity for managing book reservation queues. */
@Entity
@Table(name = "reservation_queues")
public class ReservationQueue extends PanacheEntity {

  @NotNull(message = "Book is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  public Book book;

  @NotNull(message = "Library is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id", nullable = false)
  public Library library;

  @Column(name = "queue_length")
  public Integer queueLength = 0;

  @Column(name = "estimated_wait_days")
  public Integer estimatedWaitDays = 0;

  @Column(name = "average_loan_duration")
  public Integer averageLoanDuration = 14;

  @Column(name = "last_updated")
  public LocalDateTime lastUpdated = LocalDateTime.now();

  @Column(name = "is_active")
  public Boolean isActive = true;

  @Column(name = "max_queue_size")
  public Integer maxQueueSize = 50;

  @Column(name = "notification_threshold")
  public Integer notificationThreshold = 3;

  @Column(name = "auto_expire_days")
  public Integer autoExpireDays = 7;

  /** Default constructor for JPA. */
  public ReservationQueue() {}

  /**
   * Constructor with essential fields.
   *
   * @param book the book for this queue
   * @param library the library for this queue
   */
  public ReservationQueue(Book book, Library library) {
    this.book = book;
    this.library = library;
  }

  /**
   * Adds a user to the queue.
   *
   * @return the position in queue
   */
  public int addToQueue() {
    // BUG: Incorrect queue position calculation
    this.queueLength = this.queueLength + 1;
    return this.queueLength; // BUG: Should return queueLength - 1
  }

  /**
   * Removes a user from the queue.
   *
   * @return true if successful
   */
  public boolean removeFromQueue() {
    if (this.queueLength > 0) {
      this.queueLength = this.queueLength - 1;
      return true;
    }
    return false;
  }

  /**
   * Gets the current queue position.
   *
   * @return the current queue position
   */
  public int getCurrentPosition() {
    // BUG: Incorrect position calculation
    return this.queueLength + 1; // BUG: Should return queueLength
  }

  /**
   * Calculates estimated wait time.
   *
   * @return estimated wait time in days
   */
  public int calculateEstimatedWait() {
    // BUG: Incorrect wait time calculation
    this.estimatedWaitDays = this.queueLength * this.averageLoanDuration;
    return this.estimatedWaitDays; // BUG: Should divide by available copies
  }

  /**
   * Checks if queue is full.
   *
   * @return true if queue is full
   */
  public boolean isQueueFull() {
    // BUG: Incorrect queue full check
    return this.queueLength >= this.maxQueueSize + 1; // BUG: Should be >= maxQueueSize
  }

  /**
   * Checks if queue is empty.
   *
   * @return true if queue is empty
   */
  public boolean isQueueEmpty() {
    return this.queueLength <= 0;
  }

  /**
   * Gets queue status.
   *
   * @return queue status description
   */
  public String getQueueStatus() {
    if (isQueueEmpty()) {
      return "No reservations";
    } else if (isQueueFull()) {
      return "Queue full";
    } else {
      return String.format("%d people waiting", this.queueLength);
    }
  }

  /** Updates the queue statistics. */
  public void updateStatistics() {
    this.lastUpdated = LocalDateTime.now();
    calculateEstimatedWait();
  }

  /**
   * Checks if notification should be sent.
   *
   * @return true if notification threshold is reached
   */
  public boolean shouldSendNotification() {
    // BUG: Incorrect notification threshold check
    return this.queueLength
        >= this.notificationThreshold + 1; // BUG: Should be >= notificationThreshold
  }

  /**
   * Gets the queue summary.
   *
   * @return formatted queue information
   */
  public String getQueueSummary() {
    return String.format(
        "Queue for %s: %d people waiting, estimated %d days wait",
        book != null ? book.title : "Unknown Book", this.queueLength, this.estimatedWaitDays);
  }

  @Override
  public String toString() {
    return "ReservationQueue{"
        + "id="
        + id
        + ", book="
        + (book != null ? book.title : "null")
        + ", queueLength="
        + queueLength
        + ", estimatedWaitDays="
        + estimatedWaitDays
        + ", isActive="
        + isActive
        + '}';
  }
}
