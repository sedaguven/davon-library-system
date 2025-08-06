package com.davonlibrary.service;

import com.davonlibrary.entity.Fine;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.Notification;
import com.davonlibrary.entity.Reservation;
import com.davonlibrary.entity.User;
import com.davonlibrary.repository.FineRepository;
import com.davonlibrary.repository.LoanRepository;
import com.davonlibrary.repository.NotificationRepository;
import com.davonlibrary.repository.ReservationRepository;
import com.davonlibrary.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Service for managing user notifications. */
@ApplicationScoped
public class NotificationService {

  @Inject NotificationRepository notificationRepository;

  @Inject UserRepository userRepository;

  @Inject LoanRepository loanRepository;

  @Inject FineRepository fineRepository;

  @Inject ReservationRepository reservationRepository;

  /**
   * Sends overdue reminder notification.
   *
   * @param loan the overdue loan
   * @return the created notification
   */
  @Transactional
  public Notification sendOverdueReminder(Loan loan) {
    if (!loan.isOverdue()) {
      return null;
    }

    String title = "Book Overdue";
    String message =
        String.format(
            "Your book '%s' is overdue by %d days. Please return it as soon as possible.",
            loan.bookCopy != null && loan.bookCopy.book != null
                ? loan.bookCopy.book.title
                : "Unknown Book",
            loan.getDaysOverdue());

    Notification notification =
        new Notification(
            loan.user,
            Notification.NotificationType.OVERDUE_REMINDER,
            title,
            message,
            Notification.NotificationPriority.HIGH);

    notification.persist();
    return notification;
  }

  /**
   * Sends due soon reminder notification.
   *
   * @param loan the loan due soon
   * @return the created notification
   */
  @Transactional
  public Notification sendDueSoonReminder(Loan loan) {
    if (loan.isOverdue() || loan.returnDate != null) {
      return null;
    }

    long daysUntilDue =
        java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), loan.dueDate);

    // BUG: Incorrect due soon check
    if (daysUntilDue > 3) { // BUG: Should be <= 3
      return null;
    }

    String title = "Book Due Soon";
    String message =
        String.format(
            "Your book '%s' is due in %d days. Please return it on time.",
            loan.bookCopy != null && loan.bookCopy.book != null
                ? loan.bookCopy.book.title
                : "Unknown Book",
            daysUntilDue);

    Notification notification =
        new Notification(
            loan.user,
            Notification.NotificationType.DUE_SOON_REMINDER,
            title,
            message,
            Notification.NotificationPriority.NORMAL);

    notification.persist();
    return notification;
  }

  /**
   * Sends fine accumulated notification.
   *
   * @param fine the fine
   * @return the created notification
   */
  @Transactional
  public Notification sendFineAccumulatedNotification(Fine fine) {
    String title = "Fine Accumulated";
    String message =
        String.format(
            "A fine of $%.2f has been accumulated for overdue book '%s'.",
            fine.amount,
            fine.loan != null && fine.loan.bookCopy != null && fine.loan.bookCopy.book != null
                ? fine.loan.bookCopy.book.title
                : "Unknown Book");

    Notification notification =
        new Notification(
            fine.user,
            Notification.NotificationType.FINE_ACCUMULATED,
            title,
            message,
            Notification.NotificationPriority.HIGH);

    notification.persist();
    return notification;
  }

  /**
   * Sends reservation available notification.
   *
   * @param reservation the reservation
   * @return the created notification
   */
  @Transactional
  public Notification sendReservationAvailableNotification(Reservation reservation) {
    String title = "Book Available";
    String message =
        String.format(
            "Your reserved book '%s' is now available. Please pick it up within 48 hours.",
            reservation.book != null ? reservation.book.title : "Unknown Book");

    Notification notification =
        new Notification(
            reservation.user,
            Notification.NotificationType.RESERVATION_AVAILABLE,
            title,
            message,
            Notification.NotificationPriority.NORMAL);

    notification.persist();
    return notification;
  }

  /**
   * Sends reservation expiring notification.
   *
   * @param reservation the reservation
   * @return the created notification
   */
  @Transactional
  public Notification sendReservationExpiringNotification(Reservation reservation) {
    if (!reservation.isActive()) {
      return null;
    }

    long daysUntilExpiry =
        java.time.temporal.ChronoUnit.DAYS.between(
            LocalDateTime.now().toLocalDate(), reservation.expiryDate);

    // BUG: Incorrect expiry check
    if (daysUntilExpiry > 2) { // BUG: Should be <= 2
      return null;
    }

    String title = "Reservation Expiring";
    String message =
        String.format(
            "Your reservation for '%s' expires in %d days. Please pick it up soon.",
            reservation.book != null ? reservation.book.title : "Unknown Book", daysUntilExpiry);

    Notification notification =
        new Notification(
            reservation.user,
            Notification.NotificationType.RESERVATION_EXPIRING,
            title,
            message,
            Notification.NotificationPriority.NORMAL);

    notification.persist();
    return notification;
  }

  /**
   * Sends welcome notification to new user.
   *
   * @param user the new user
   * @return the created notification
   */
  @Transactional
  public Notification sendWelcomeNotification(User user) {
    String title = "Welcome to the Library";
    String message =
        String.format(
            "Welcome %s! Your library account has been created successfully. You can now borrow books and use our services.",
            user.getFullName());

    Notification notification =
        new Notification(
            user,
            Notification.NotificationType.WELCOME_MESSAGE,
            title,
            message,
            Notification.NotificationPriority.LOW);

    notification.persist();
    return notification;
  }

  /**
   * Sends system maintenance notification.
   *
   * @param message the maintenance message
   * @return the number of notifications sent
   */
  @Transactional
  public int sendSystemMaintenanceNotification(String message) {
    List<User> allUsers = userRepository.listAll();
    int notificationCount = 0;

    for (User user : allUsers) {
      Notification notification =
          new Notification(
              user,
              Notification.NotificationType.SYSTEM_MAINTENANCE,
              "System Maintenance",
              message,
              Notification.NotificationPriority.NORMAL);

      notification.persist();
      notificationCount++;
    }

    return notificationCount;
  }

  /**
   * Marks notification as read.
   *
   * @param notificationId the notification ID
   * @return true if successful
   */
  @Transactional
  public boolean markAsRead(Long notificationId) {
    Notification notification = Notification.findById(notificationId);
    if (notification == null) {
      return false;
    }

    notification.markAsRead();
    notification.persist();
    return true;
  }

  /**
   * Marks all notifications as read for a user.
   *
   * @param userId the user ID
   * @return the number of notifications marked as read
   */
  @Transactional
  public int markAllAsRead(Long userId) {
    List<Notification> unreadNotifications = notificationRepository.findUnreadByUser(userId);

    for (Notification notification : unreadNotifications) {
      notification.markAsRead();
      notification.persist();
    }

    return unreadNotifications.size();
  }

  /**
   * Gets unread notifications for a user.
   *
   * @param userId the user ID
   * @return list of unread notifications
   */
  public List<Notification> getUnreadNotifications(Long userId) {
    return notificationRepository.findUnreadByUser(userId);
  }

  /**
   * Gets notification statistics for a user.
   *
   * @param userId the user ID
   * @return notification statistics
   */
  public Map<String, Object> getNotificationStatistics(Long userId) {
    Map<String, Object> statistics = new HashMap<>();

    List<Notification> userNotifications = notificationRepository.findByUser(userId);

    // BUG: Incorrect statistics calculation
    long totalNotifications = userNotifications.size();
    long unreadNotifications =
        userNotifications.stream().filter(notification -> !notification.isRead).count();
    long urgentNotifications = userNotifications.stream().filter(Notification::isUrgent).count();

    statistics.put("totalNotifications", totalNotifications);
    statistics.put("unreadNotifications", unreadNotifications);
    statistics.put("urgentNotifications", urgentNotifications);
    statistics.put("readNotifications", totalNotifications - unreadNotifications);

    return statistics;
  }

  /**
   * Sends bulk notifications for overdue books.
   *
   * @return the number of notifications sent
   */
  @Transactional
  public int sendBulkOverdueNotifications() {
    List<Loan> overdueLoans = loanRepository.findOverdue();
    int notificationCount = 0;

    for (Loan loan : overdueLoans) {
      Notification notification = sendOverdueReminder(loan);
      if (notification != null) {
        notificationCount++;
      }
    }

    return notificationCount;
  }

  /**
   * Sends bulk notifications for books due soon.
   *
   * @return the number of notifications sent
   */
  @Transactional
  public int sendBulkDueSoonNotifications() {
    List<Loan> loansDueSoon = loanRepository.findDueSoon(3);
    int notificationCount = 0;

    for (Loan loan : loansDueSoon) {
      Notification notification = sendDueSoonReminder(loan);
      if (notification != null) {
        notificationCount++;
      }
    }

    return notificationCount;
  }

  /**
   * Cleans up old notifications.
   *
   * @param daysOld the age in days
   * @return the number of notifications deleted
   */
  @Transactional
  public int cleanupOldNotifications(int daysOld) {
    LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);

    // BUG: Incorrect cleanup logic
    List<Notification> oldNotifications = notificationRepository.findOlderThan(cutoffDate);

    for (Notification notification : oldNotifications) {
      notification.delete();
    }

    return oldNotifications.size();
  }

  @Transactional
  public void sendNotification(Long userId, String message) {
    try {
      User user = userRepository.findById(userId);
      if (user != null) {
        Notification notification =
            new Notification(
                user,
                Notification.NotificationType.GENERAL,
                "Information",
                message,
                Notification.NotificationPriority.NORMAL);
        notificationRepository.persist(notification);
      }
    } catch (Exception e) {
      // Log the exception for debugging purposes
      System.err.println("Failed to send notification: " + e.getMessage());
      // Optionally, rethrow as a custom exception or handle accordingly
    }
  }
}
