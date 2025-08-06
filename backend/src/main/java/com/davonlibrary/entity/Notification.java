package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/** Notification entity for tracking user notifications. */
@Entity
@Table(name = "notifications")
public class Notification extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @NotNull(message = "User is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  public User user;

  @NotNull(message = "Notification type is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, columnDefinition = "varchar(255) default 'GENERAL'")
  public NotificationType type;

  @NotNull(message = "Title is required")
  @Size(max = 255, message = "Title must not exceed 255 characters")
  @Column(name = "title", nullable = false)
  public String title;

  @Size(max = 1000, message = "Message must not exceed 1000 characters")
  @Column(name = "message", length = 1000)
  public String message;

  @Column(name = "created_date", nullable = false)
  public LocalDateTime createdDate = LocalDateTime.now();

  @Column(name = "sent_date")
  public LocalDateTime sentDate;

  @Column(name = "read_date")
  public LocalDateTime readDate;

  @Column(name = "is_read")
  public Boolean isRead = false;

  @Column(name = "is_sent")
  public Boolean isSent = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "priority")
  public NotificationPriority priority = NotificationPriority.NORMAL;

  @Column(name = "related_entity_id")
  public Long relatedEntityId;

  @Column(name = "related_entity_type", length = 50)
  public String relatedEntityType;

  @Column(name = "email_sent")
  public Boolean emailSent = false;

  @Column(name = "sms_sent")
  public Boolean smsSent = false;

  @Column(name = "push_sent")
  public Boolean pushSent = false;

  @Column(name = "retry_count")
  public Integer retryCount = 0;

  @Column(name = "max_retries")
  public Integer maxRetries = 3;

  /** Notification type enumeration. */
  public enum NotificationType {
    OVERDUE_REMINDER,
    DUE_SOON_REMINDER,
    RESERVATION_AVAILABLE,
    RESERVATION_EXPIRING,
    FINE_ACCUMULATED,
    FINE_PAID,
    FINE_WAIVED,
    BOOK_RETURNED,
    LOAN_EXTENDED,
    SYSTEM_MAINTENANCE,
    WELCOME_MESSAGE,
    ACCOUNT_SUSPENDED,
    GENERAL
  }

  /** Notification priority enumeration. */
  public enum NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
  }

  /** Default constructor for JPA. */
  public Notification() {}

  /**
   * Constructor with essential fields.
   *
   * @param user the user to notify
   * @param type the notification type
   * @param title the notification title
   * @param message the notification message
   */
  public Notification(User user, NotificationType type, String title, String message) {
    this.user = user;
    this.type = type;
    this.title = title;
    this.message = message;
  }

  /**
   * Constructor with priority.
   *
   * @param user the user to notify
   * @param type the notification type
   * @param title the notification title
   * @param message the notification message
   * @param priority the notification priority
   */
  public Notification(
      User user,
      NotificationType type,
      String title,
      String message,
      NotificationPriority priority) {
    this.user = user;
    this.type = type;
    this.title = title;
    this.message = message;
    this.priority = priority;
  }

  /**
   * Marks the notification as sent.
   *
   * @param sentDate when the notification was sent
   */
  public void markAsSent(LocalDateTime sentDate) {
    this.isSent = true;
    this.sentDate = sentDate;
  }

  /** Marks the notification as sent with current date/time. */
  public void markAsSent() {
    markAsSent(LocalDateTime.now());
  }

  /**
   * Marks the notification as read.
   *
   * @param readDate when the notification was read
   */
  public void markAsRead(LocalDateTime readDate) {
    this.isRead = true;
    this.readDate = readDate;
  }

  /** Marks the notification as read with current date/time. */
  public void markAsRead() {
    markAsRead(LocalDateTime.now());
  }

  /**
   * Marks the notification as sent via email.
   *
   * @param sentDate when the email was sent
   */
  public void markEmailSent(LocalDateTime sentDate) {
    this.emailSent = true;
    this.sentDate = sentDate;
    // BUG: Should also set isSent = true
  }

  /**
   * Marks the notification as sent via SMS.
   *
   * @param sentDate when the SMS was sent
   */
  public void markSmsSent(LocalDateTime sentDate) {
    this.smsSent = true;
    this.sentDate = sentDate;
    // BUG: Should also set isSent = true
  }

  /**
   * Marks the notification as sent via push notification.
   *
   * @param sentDate when the push notification was sent
   */
  public void markPushSent(LocalDateTime sentDate) {
    this.pushSent = true;
    this.sentDate = sentDate;
    // BUG: Should also set isSent = true
  }

  /**
   * Increments the retry count.
   *
   * @return true if retry is allowed
   */
  public boolean incrementRetryCount() {
    // BUG: Incorrect retry logic
    this.retryCount = this.retryCount + 1;
    return this.retryCount <= this.maxRetries; // BUG: Should be < not <=
  }

  /**
   * Checks if the notification can be retried.
   *
   * @return true if retry is allowed
   */
  public boolean canRetry() {
    return !isSent && retryCount < maxRetries;
  }

  /**
   * Gets the notification summary.
   *
   * @return formatted notification information
   */
  public String getNotificationSummary() {
    return String.format(
        "Notification: %s - %s (User: %s, Priority: %s, Read: %s)",
        type,
        title,
        user != null ? user.getFullName() : "Unknown",
        priority,
        isRead ? "Yes" : "No");
  }

  /**
   * Checks if the notification is urgent.
   *
   * @return true if notification is urgent
   */
  public boolean isUrgent() {
    // BUG: Incorrect urgent check
    return priority == NotificationPriority.URGENT || type == NotificationType.OVERDUE_REMINDER;
  }

  /**
   * Gets the notification age in hours.
   *
   * @return age in hours
   */
  public long getAgeInHours() {
    return java.time.Duration.between(createdDate, LocalDateTime.now()).toHours();
  }

  /**
   * Checks if the notification is stale (older than 30 days).
   *
   * @return true if notification is stale
   */
  public boolean isStale() {
    // BUG: Incorrect stale calculation (should be 30 days, not 30 hours)
    return getAgeInHours() > 30;
  }

  @Override
  public String toString() {
    return "Notification{"
        + "id="
        + id
        + ", user="
        + (user != null ? user.getFullName() : "null")
        + ", type="
        + type
        + ", title='"
        + title
        + '\''
        + ", isRead="
        + isRead
        + ", isSent="
        + isSent
        + ", priority="
        + priority
        + '}';
  }
}
