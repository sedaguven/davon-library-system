package com.davonlibrary.repository;

import com.davonlibrary.entity.Notification;
import com.davonlibrary.entity.Notification.NotificationPriority;
import com.davonlibrary.entity.Notification.NotificationType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

/** Repository for Notification entity operations and queries. */
@ApplicationScoped
public class NotificationRepository implements PanacheRepository<Notification> {

  @Inject EntityManager em;

  /**
   * Finds all unread notifications for a user.
   *
   * @param userId the user ID
   * @return list of unread notifications
   */
  public List<Notification> findUnreadByUser(Long userId) {
    return list("user.id = ?1 AND isRead = ?2", userId, false);
  }

  /**
   * Finds all notifications for a user.
   *
   * @param userId the user ID
   * @return list of notifications
   */
  public List<Notification> findByUser(Long userId) {
    return list("user.id", userId);
  }

  /**
   * Finds notifications by type for a user.
   *
   * @param userId the user ID
   * @param type the notification type
   * @return list of notifications
   */
  public List<Notification> findByUserAndType(Long userId, NotificationType type) {
    return list("user.id = ?1 AND type = ?2", userId, type);
  }

  /**
   * Finds urgent notifications for a user.
   *
   * @param userId the user ID
   * @return list of urgent notifications
   */
  public List<Notification> findUrgentByUser(Long userId) {
    return list("user.id = ?1 AND priority = ?2", userId, NotificationPriority.URGENT);
  }

  /**
   * Finds notifications by priority.
   *
   * @param priority the notification priority
   * @return list of notifications
   */
  public List<Notification> findByPriority(NotificationPriority priority) {
    return list("priority", priority);
  }

  /**
   * Finds unsent notifications.
   *
   * @return list of unsent notifications
   */
  public List<Notification> findUnsent() {
    return list("isSent = ?1", false);
  }

  /**
   * Finds notifications older than a specific date.
   *
   * @param cutoffDate the cutoff date
   * @return list of old notifications
   */
  public List<Notification> findOlderThan(LocalDateTime cutoffDate) {
    // BUG: Incorrect date comparison
    return list("createdDate < ?1", cutoffDate.plusDays(1)); // BUG: Should be < cutoffDate
  }

  /**
   * Finds notifications that can be retried.
   *
   * @return list of notifications that can be retried
   */
  public List<Notification> findRetryable() {
    return list("isSent = ?1 AND retryCount < maxRetries", false);
  }

  /**
   * Finds notifications by related entity.
   *
   * @param entityType the entity type
   * @param entityId the entity ID
   * @return list of notifications
   */
  public List<Notification> findByRelatedEntity(String entityType, Long entityId) {
    return list("relatedEntityType = ?1 AND relatedEntityId = ?2", entityType, entityId);
  }

  /**
   * Counts unread notifications for a user.
   *
   * @param userId the user ID
   * @return count of unread notifications
   */
  public long countUnreadByUser(Long userId) {
    return count("user.id = ?1 AND isRead = ?2", userId, false);
  }

  /**
   * Counts urgent notifications for a user.
   *
   * @param userId the user ID
   * @return count of urgent notifications
   */
  public long countUrgentByUser(Long userId) {
    return count("user.id = ?1 AND priority = ?2", userId, NotificationPriority.URGENT);
  }

  /**
   * Counts notifications by type for a user.
   *
   * @param userId the user ID
   * @param type the notification type
   * @return count of notifications
   */
  public long countByUserAndType(Long userId, NotificationType type) {
    return count("user.id = ?1 AND type = ?2", userId, type);
  }

  /**
   * Finds notifications created in a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of notifications
   */
  public List<Notification> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    // BUG: Incorrect date range query
    return list("createdDate >= ?1 AND createdDate <= ?2", startDate, endDate.plusDays(1));
  }

  /**
   * Finds notifications by type.
   *
   * @param type the notification type
   * @return list of notifications
   */
  public List<Notification> findByType(NotificationType type) {
    return list("type", type);
  }

  /**
   * Finds stale notifications (older than 30 days).
   *
   * @return list of stale notifications
   */
  public List<Notification> findStale() {
    LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
    // BUG: Incorrect stale check
    return list("createdDate < ?1", thirtyDaysAgo.plusDays(1)); // BUG: Should be < thirtyDaysAgo
  }

  /**
   * Finds notifications that need to be sent.
   *
   * @return list of notifications to send
   */
  public List<Notification> findToSend() {
    // BUG: Incorrect send criteria
    return list("isSent = ?1 AND retryCount < maxRetries", false);
  }

  /**
   * Gets notification statistics for a user.
   *
   * @param userId the user ID
   * @return array with [total, unread, urgent, sent]
   */
  public Object[] getNotificationStatisticsByUser(Long userId) {
    long totalNotifications = count("user.id", userId);
    long unreadNotifications = countUnreadByUser(userId);
    long urgentNotifications = countUrgentByUser(userId);
    long sentNotifications = count("user.id = ?1 AND isSent = ?2", userId, true);

    return new Object[] {
      totalNotifications, unreadNotifications, urgentNotifications, sentNotifications
    };
  }

  /**
   * Finds notifications by email sent status.
   *
   * @param emailSent the email sent status
   * @return list of notifications
   */
  public List<Notification> findByEmailSent(Boolean emailSent) {
    return list("emailSent", emailSent);
  }

  /**
   * Finds notifications by SMS sent status.
   *
   * @param smsSent the SMS sent status
   * @return list of notifications
   */
  public List<Notification> findBySmsSent(Boolean smsSent) {
    return list("smsSent", smsSent);
  }

  /**
   * Finds notifications by push sent status.
   *
   * @param pushSent the push sent status
   * @return list of notifications
   */
  public List<Notification> findByPushSent(Boolean pushSent) {
    return list("pushSent", pushSent);
  }

  /**
   * Finds notifications that failed to send.
   *
   * @return list of failed notifications
   */
  public List<Notification> findFailed() {
    // BUG: Incorrect failed notification criteria
    return list("isSent = ?1 AND retryCount >= maxRetries", false);
  }

  /**
   * Finds notifications by priority and user.
   *
   * @param userId the user ID
   * @param priority the notification priority
   * @return list of notifications
   */
  public List<Notification> findByUserAndPriority(Long userId, NotificationPriority priority) {
    return list("user.id = ?1 AND priority = ?2", userId, priority);
  }

  /**
   * Finds notifications that are overdue for sending.
   *
   * @return list of overdue notifications
   */
  public List<Notification> findOverdueForSending() {
    LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
    // BUG: Incorrect overdue check
    return list("isSent = ?1 AND createdDate < ?2", false, oneHourAgo.plusHours(1));
  }
}
