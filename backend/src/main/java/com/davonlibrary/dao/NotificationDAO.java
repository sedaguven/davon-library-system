package com.davonlibrary.dao;

import com.davonlibrary.entity.Notification;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NotificationDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDAO.class);

  @Inject EntityManager entityManager;

  @Transactional
  public Optional<Notification> findById(Long id) {
    try {
      Notification notification = entityManager.find(Notification.class, id);
      return Optional.ofNullable(notification);
    } catch (Exception e) {
      LOGGER.error("Error finding notification by ID: {}", id, e);
      return Optional.empty();
    }
  }

  @Transactional
  public List<Notification> findByUserId(Long userId) {
    try {
      TypedQuery<Notification> query =
          entityManager.createQuery(
              "SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC",
              Notification.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding notifications by user ID: {}", userId, e);
      return List.of();
    }
  }

  @Transactional
  public List<Notification> findUnreadByUserId(Long userId) {
    try {
      TypedQuery<Notification> query =
          entityManager.createQuery(
              "SELECT n FROM Notification n WHERE n.user.id = :userId "
                  + "AND n.isRead = false ORDER BY n.createdAt DESC",
              Notification.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding unread notifications by user ID: {}", userId, e);
      return List.of();
    }
  }

  @Transactional
  public Notification save(Notification notification) {
    try {
      entityManager.persist(notification);
      entityManager.flush();
      LOGGER.info(
          "Notification saved successfully: User={}, Type={}",
          notification.user.id,
          notification.type);
      return notification;
    } catch (Exception e) {
      LOGGER.error(
          "Error saving notification: User={}, Type={}",
          notification.user.id,
          notification.type,
          e);
      throw new RuntimeException("Failed to save notification", e);
    }
  }

  @Transactional
  public Notification update(Notification notification) {
    try {
      Notification updatedNotification = entityManager.merge(notification);
      LOGGER.info("Notification updated successfully: ID={}", notification.id);
      return updatedNotification;
    } catch (Exception e) {
      LOGGER.error("Error updating notification: ID={}", notification.id, e);
      throw new RuntimeException("Failed to update notification", e);
    }
  }

  @Transactional
  public boolean deleteById(Long id) {
    try {
      Notification notification = entityManager.find(Notification.class, id);
      if (notification != null) {
        entityManager.remove(notification);
        LOGGER.info("Notification deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting notification with ID: {}", id, e);
      throw new RuntimeException("Failed to delete notification", e);
    }
  }

  @Transactional
  public List<Notification> findAll() {
    try {
      TypedQuery<Notification> query =
          entityManager.createQuery(
              "SELECT n FROM Notification n ORDER BY n.createdAt DESC", Notification.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all notifications", e);
      return List.of();
    }
  }

  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(n) FROM Notification n", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting notifications", e);
      return 0;
    }
  }

  @Transactional
  public long countUnreadByUserId(Long userId) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId "
                  + "AND n.isRead = false",
              Long.class);
      query.setParameter("userId", userId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting unread notifications for user: {}", userId, e);
      return 0;
    }
  }
}
