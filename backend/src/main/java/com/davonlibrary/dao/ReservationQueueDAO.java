package com.davonlibrary.dao;

import com.davonlibrary.entity.ReservationQueue;
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
public class ReservationQueueDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReservationQueueDAO.class);

  @Inject EntityManager entityManager;

  @Transactional
  public Optional<ReservationQueue> findById(Long id) {
    try {
      ReservationQueue queue = entityManager.find(ReservationQueue.class, id);
      return Optional.ofNullable(queue);
    } catch (Exception e) {
      LOGGER.error("Error finding reservation queue by ID: {}", id, e);
      return Optional.empty();
    }
  }

  @Transactional
  public List<ReservationQueue> findByBookId(Long bookId) {
    try {
      TypedQuery<ReservationQueue> query =
          entityManager.createQuery(
              "SELECT rq FROM ReservationQueue rq WHERE rq.book.id = :bookId ORDER BY rq.position",
              ReservationQueue.class);
      query.setParameter("bookId", bookId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reservation queue by book ID: {}", bookId, e);
      return List.of();
    }
  }

  @Transactional
  public ReservationQueue save(ReservationQueue queue) {
    try {
      entityManager.persist(queue);
      entityManager.flush();
      LOGGER.info(
          "Reservation queue saved successfully: Book={}, Length={}",
          queue.book.title,
          queue.queueLength);
      return queue;
    } catch (Exception e) {
      LOGGER.error(
          "Error saving reservation queue: Book={}, Length={}",
          queue.book.title,
          queue.queueLength,
          e);
      throw new RuntimeException("Failed to save reservation queue", e);
    }
  }

  @Transactional
  public ReservationQueue update(ReservationQueue queue) {
    try {
      ReservationQueue updatedQueue = entityManager.merge(queue);
      LOGGER.info("Reservation queue updated successfully: ID={}", queue.id);
      return updatedQueue;
    } catch (Exception e) {
      LOGGER.error("Error updating reservation queue: ID={}", queue.id, e);
      throw new RuntimeException("Failed to update reservation queue", e);
    }
  }

  @Transactional
  public boolean deleteById(Long id) {
    try {
      ReservationQueue queue = entityManager.find(ReservationQueue.class, id);
      if (queue != null) {
        entityManager.remove(queue);
        LOGGER.info("Reservation queue deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting reservation queue with ID: {}", id, e);
      throw new RuntimeException("Failed to delete reservation queue", e);
    }
  }

  @Transactional
  public List<ReservationQueue> findAll() {
    try {
      TypedQuery<ReservationQueue> query =
          entityManager.createQuery(
              "SELECT rq FROM ReservationQueue rq ORDER BY rq.book.title, rq.position",
              ReservationQueue.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all reservation queues", e);
      return List.of();
    }
  }

  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(rq) FROM ReservationQueue rq", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting reservation queues", e);
      return 0;
    }
  }
}
