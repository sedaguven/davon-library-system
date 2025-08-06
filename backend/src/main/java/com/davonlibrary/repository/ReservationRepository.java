package com.davonlibrary.repository;

import com.davonlibrary.entity.Reservation;
import com.davonlibrary.entity.Reservation.ReservationStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Repository for Reservation entity operations and queries. */
@ApplicationScoped
public class ReservationRepository implements PanacheRepository<Reservation> {

  @Inject EntityManager em;

  /**
   * Finds all active reservations.
   *
   * @return list of active reservations
   */
  public List<Reservation> findActive() {
    return list("status = ?1", ReservationStatus.ACTIVE);
  }

  /**
   * Finds active reservations for a specific user.
   *
   * @param userId the user ID
   * @return list of active reservations for the user
   */
  public List<Reservation> findActiveByUser(Long userId) {
    return list("user.id = ?1 AND status = ?2", userId, ReservationStatus.ACTIVE);
  }

  /**
   * Finds all reservations for a specific user.
   *
   * @param userId the user ID
   * @return list of all reservations for the user
   */
  public List<Reservation> findByUser(Long userId) {
    return list("user.id", userId);
  }

  /**
   * Finds active reservations for a specific book.
   *
   * @param bookId the book ID
   * @return list of active reservations for the book
   */
  public List<Reservation> findActiveByBook(Long bookId) {
    return list("book.id = ?1 AND status = ?2", bookId, ReservationStatus.ACTIVE);
  }

  /**
   * Finds all reservations for a specific book.
   *
   * @param bookId the book ID
   * @return list of all reservations for the book
   */
  public List<Reservation> findByBook(Long bookId) {
    return list("book.id", bookId);
  }

  /**
   * Finds reservations expiring within specified days.
   *
   * @param days number of days ahead to check
   * @return list of reservations expiring soon
   */
  public List<Reservation> findExpiringSoon(int days) {
    LocalDate expiryLimit = LocalDate.now().plusDays(days);
    return list(
        "status = ?1 AND expiryDate <= ?2 AND expiryDate >= ?3",
        ReservationStatus.ACTIVE,
        expiryLimit,
        LocalDate.now());
  }

  /**
   * Finds expired reservations.
   *
   * @return list of expired reservations
   */
  public List<Reservation> findExpired() {
    return list("status = ?1 AND expiryDate < ?2", ReservationStatus.ACTIVE, LocalDate.now());
  }

  /**
   * Finds reservations by status.
   *
   * @param status the reservation status
   * @return list of reservations with the specified status
   */
  public List<Reservation> findByStatus(ReservationStatus status) {
    return list("status", status);
  }

  /**
   * Finds reservations created within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of reservations created in the date range
   */
  public List<Reservation> findCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return list("reservationDate >= ?1 AND reservationDate <= ?2", startDate, endDate);
  }

  /**
   * Gets the next reservation in queue for a book.
   *
   * @param bookId the book ID
   * @return the next reservation if exists
   */
  public Optional<Reservation> getNextInQueue(Long bookId) {
    return find(
            "book.id = ?1 AND status = ?2 ORDER BY queuePosition ASC, reservationDate ASC",
            bookId,
            ReservationStatus.ACTIVE)
        .firstResultOptional();
  }

  /**
   * Gets the current queue for a book.
   *
   * @param bookId the book ID
   * @return list of reservations in queue order
   */
  public List<Reservation> getQueueForBook(Long bookId) {
    return list(
        "book.id = ?1 AND status = ?2 ORDER BY queuePosition ASC, reservationDate ASC",
        bookId,
        ReservationStatus.ACTIVE);
  }

  /**
   * Checks if a user has an active reservation for a specific book.
   *
   * @param userId the user ID
   * @param bookId the book ID
   * @return true if user has an active reservation for the book
   */
  public boolean hasActiveReservationForBook(Long userId, Long bookId) {
    return count(
            "user.id = ?1 AND book.id = ?2 AND status = ?3",
            userId,
            bookId,
            ReservationStatus.ACTIVE)
        > 0;
  }

  /**
   * Counts active reservations for a user.
   *
   * @param userId the user ID
   * @return number of active reservations
   */
  public long countActiveByUser(Long userId) {
    return count("user.id = ?1 AND status = ?2", userId, ReservationStatus.ACTIVE);
  }

  /**
   * Counts active reservations for a book.
   *
   * @param bookId the book ID
   * @return number of active reservations
   */
  public long countActiveByBook(Long bookId) {
    return count("book.id = ?1 AND status = ?2", bookId, ReservationStatus.ACTIVE);
  }

  /**
   * Gets the queue position for a user's reservation of a specific book.
   *
   * @param userId the user ID
   * @param bookId the book ID
   * @return the queue position if reservation exists
   */
  public Optional<Integer> getQueuePosition(Long userId, Long bookId) {
    List<Integer> result =
        em.createQuery(
                "SELECT r.queuePosition FROM Reservation r WHERE r.user.id = ?1 AND r.book.id = ?2 AND r.status = ?3",
                Integer.class)
            .setParameter(1, userId)
            .setParameter(2, bookId)
            .setParameter(3, ReservationStatus.ACTIVE)
            .getResultList();

    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  /**
   * Gets reservation statistics.
   *
   * @return reservation statistics
   */
  public ReservationStats getReservationStats() {
    long totalReservations = count();
    long activeReservations = count("status = ?1", ReservationStatus.ACTIVE);
    long fulfilledReservations = count("status = ?1", ReservationStatus.FULFILLED);
    long expiredReservations = count("status = ?1", ReservationStatus.EXPIRED);
    long cancelledReservations = count("status = ?1", ReservationStatus.CANCELLED);

    return new ReservationStats(
        totalReservations,
        activeReservations,
        fulfilledReservations,
        expiredReservations,
        cancelledReservations);
  }

  /** Reservation statistics DTO. */
  public static class ReservationStats {
    public final long totalReservations;
    public final long activeReservations;
    public final long fulfilledReservations;
    public final long expiredReservations;
    public final long cancelledReservations;

    public ReservationStats(
        long totalReservations,
        long activeReservations,
        long fulfilledReservations,
        long expiredReservations,
        long cancelledReservations) {
      this.totalReservations = totalReservations;
      this.activeReservations = activeReservations;
      this.fulfilledReservations = fulfilledReservations;
      this.expiredReservations = expiredReservations;
      this.cancelledReservations = cancelledReservations;
    }
  }
}
