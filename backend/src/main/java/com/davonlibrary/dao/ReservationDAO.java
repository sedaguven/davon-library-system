package com.davonlibrary.dao;

import com.davonlibrary.entity.Reservation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for Reservation entity operations. Provides a clean abstraction layer over
 * database operations.
 */
@ApplicationScoped
public class ReservationDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReservationDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a reservation by ID.
   *
   * @param id the reservation ID
   * @return Optional containing the reservation if found
   */
  @Transactional
  public Optional<Reservation> findById(Long id) {
    try {
      Reservation reservation = entityManager.find(Reservation.class, id);
      return Optional.ofNullable(reservation);
    } catch (Exception e) {
      LOGGER.error("Error finding reservation by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find all reservations for a user.
   *
   * @param userId the user ID
   * @return list of reservations for the user
   */
  @Transactional
  public List<Reservation> findByUserId(Long userId) {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.user.id = :userId ORDER BY r.reservationDate DESC",
              Reservation.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reservations by user ID: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Find active reservations for a user.
   *
   * @param userId the user ID
   * @return list of active reservations
   */
  @Transactional
  public List<Reservation> findActiveByUserId(Long userId) {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.user.id = :userId "
                  + "AND r.status.statusName = 'ACTIVE' ORDER BY r.reservationDate",
              Reservation.class);
      query.setParameter("userId", userId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding active reservations by user ID: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Find reservations for a book.
   *
   * @param bookId the book ID
   * @return list of reservations for the book
   */
  @Transactional
  public List<Reservation> findByBookId(Long bookId) {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.book.id = :bookId "
                  + "ORDER BY r.reservationDate",
              Reservation.class);
      query.setParameter("bookId", bookId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reservations by book ID: {}", bookId, e);
      return List.of();
    }
  }

  /**
   * Find active reservations for a book.
   *
   * @param bookId the book ID
   * @return list of active reservations
   */
  @Transactional
  public List<Reservation> findActiveByBookId(Long bookId) {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.book.id = :bookId "
                  + "AND r.status.statusName = 'ACTIVE' ORDER BY r.reservationDate",
              Reservation.class);
      query.setParameter("bookId", bookId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding active reservations by book ID: {}", bookId, e);
      return List.of();
    }
  }

  /**
   * Find reservations by status.
   *
   * @param statusName the status name
   * @return list of reservations with the specified status
   */
  @Transactional
  public List<Reservation> findByStatus(String statusName) {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.status.statusName = :statusName "
                  + "ORDER BY r.reservationDate DESC",
              Reservation.class);
      query.setParameter("statusName", statusName);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reservations by status: {}", statusName, e);
      return List.of();
    }
  }

  /**
   * Find expired reservations.
   *
   * @return list of expired reservations
   */
  @Transactional
  public List<Reservation> findExpired() {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.expiryDate < CURRENT_DATE "
                  + "AND r.status.statusName = 'ACTIVE' ORDER BY r.expiryDate",
              Reservation.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding expired reservations", e);
      return List.of();
    }
  }

  /**
   * Find reservations expiring soon.
   *
   * @param days number of days to look ahead
   * @return list of reservations expiring soon
   */
  @Transactional
  public List<Reservation> findExpiringSoon(int days) {
    try {
      LocalDate expiryDate = LocalDate.now().plusDays(days);
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.expiryDate <= :expiryDate "
                  + "AND r.status.statusName = 'ACTIVE' ORDER BY r.expiryDate",
              Reservation.class);
      query.setParameter("expiryDate", expiryDate);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reservations expiring soon", e);
      return List.of();
    }
  }

  /**
   * Find reservations within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of reservations within the date range
   */
  @Transactional
  public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r WHERE r.reservationDate >= :startDate "
                  + "AND r.reservationDate <= :endDate ORDER BY r.reservationDate DESC",
              Reservation.class);
      query.setParameter("startDate", startDate);
      query.setParameter("endDate", endDate);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding reservations by date range: {} to {}", startDate, endDate, e);
      return List.of();
    }
  }

  /**
   * Save a new reservation.
   *
   * @param reservation the reservation to save
   * @return the saved reservation with generated ID
   */
  @Transactional
  public Reservation save(Reservation reservation) {
    try {
      entityManager.persist(reservation);
      entityManager.flush();
      LOGGER.info(
          "Reservation saved successfully: User={}, Book={}",
          reservation.user.id,
          reservation.book.title);
      return reservation;
    } catch (Exception e) {
      LOGGER.error(
          "Error saving reservation: User={}, Book={}",
          reservation.user.id,
          reservation.book.title,
          e);
      throw new RuntimeException("Failed to save reservation", e);
    }
  }

  /**
   * Update an existing reservation.
   *
   * @param reservation the reservation to update
   * @return the updated reservation
   */
  @Transactional
  public Reservation update(Reservation reservation) {
    try {
      Reservation updatedReservation = entityManager.merge(reservation);
      LOGGER.info("Reservation updated successfully: ID={}", reservation.id);
      return updatedReservation;
    } catch (Exception e) {
      LOGGER.error("Error updating reservation: ID={}", reservation.id, e);
      throw new RuntimeException("Failed to update reservation", e);
    }
  }

  /**
   * Delete a reservation by ID.
   *
   * @param id the reservation ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      Reservation reservation = entityManager.find(Reservation.class, id);
      if (reservation != null) {
        entityManager.remove(reservation);
        LOGGER.info("Reservation deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting reservation with ID: {}", id, e);
      throw new RuntimeException("Failed to delete reservation", e);
    }
  }

  /**
   * Find all reservations.
   *
   * @return list of all reservations
   */
  @Transactional
  public List<Reservation> findAll() {
    try {
      TypedQuery<Reservation> query =
          entityManager.createQuery(
              "SELECT r FROM Reservation r ORDER BY r.reservationDate DESC", Reservation.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all reservations", e);
      return List.of();
    }
  }

  /**
   * Count total number of reservations.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting reservations", e);
      return 0;
    }
  }

  /**
   * Count active reservations for a user.
   *
   * @param userId the user ID
   * @return number of active reservations
   */
  @Transactional
  public long countActiveByUserId(Long userId) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId "
                  + "AND r.status.statusName = 'ACTIVE'",
              Long.class);
      query.setParameter("userId", userId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting active reservations for user: {}", userId, e);
      return 0;
    }
  }

  /**
   * Count active reservations for a book.
   *
   * @param bookId the book ID
   * @return number of active reservations
   */
  @Transactional
  public long countActiveByBookId(Long bookId) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(r) FROM Reservation r WHERE r.book.id = :bookId "
                  + "AND r.status.statusName = 'ACTIVE'",
              Long.class);
      query.setParameter("bookId", bookId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting active reservations for book: {}", bookId, e);
      return 0;
    }
  }

  /**
   * Get reservation statistics.
   *
   * @return array with [total reservations, active reservations, expired reservations, fulfilled
   *     reservations]
   */
  @Transactional
  public Object[] getReservationStatistics() {
    try {
      // Total reservations
      TypedQuery<Long> totalQuery =
          entityManager.createQuery("SELECT COUNT(r) FROM Reservation r", Long.class);
      long totalReservations = totalQuery.getSingleResult();

      // Active reservations
      TypedQuery<Long> activeQuery =
          entityManager.createQuery(
              "SELECT COUNT(r) FROM Reservation r WHERE r.status.statusName = 'ACTIVE'",
              Long.class);
      long activeReservations = activeQuery.getSingleResult();

      // Expired reservations
      TypedQuery<Long> expiredQuery =
          entityManager.createQuery(
              "SELECT COUNT(r) FROM Reservation r WHERE r.expiryDate < CURRENT_DATE "
                  + "AND r.status.statusName = 'ACTIVE'",
              Long.class);
      long expiredReservations = expiredQuery.getSingleResult();

      // Fulfilled reservations
      TypedQuery<Long> fulfilledQuery =
          entityManager.createQuery(
              "SELECT COUNT(r) FROM Reservation r WHERE r.status.statusName = 'FULFILLED'",
              Long.class);
      long fulfilledReservations = fulfilledQuery.getSingleResult();

      return new Object[] {
        totalReservations, activeReservations, expiredReservations, fulfilledReservations
      };
    } catch (Exception e) {
      LOGGER.error("Error getting reservation statistics", e);
      return new Object[] {0L, 0L, 0L, 0L};
    }
  }
}
