package com.davonlibrary.service;

import com.davonlibrary.dto.ReservationDTO;
import com.davonlibrary.entity.Reservation;
import com.davonlibrary.repository.ReservationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReservationService {

  @Inject ReservationRepository reservationRepository;

  @Transactional
  public Reservation createReservation(Reservation reservation) {
    if (!isValidReservation(reservation)) {
      throw new IllegalArgumentException("Invalid reservation details provided.");
    }
    // Prevent duplicate active reservations for the same user and book
    if (reservation.user != null
        && reservation.book != null
        && reservationRepository.hasActiveReservationForBook(reservation.user.id, reservation.book.id)) {
      throw new WebApplicationException(
          "You already have an active reservation for this book.", Response.Status.CONFLICT);
    }
    try {
      reservationRepository.persist(reservation);
      return reservation;
    } catch (Exception e) {
      // Log the exception for debugging purposes
      System.err.println("Failed to create reservation: " + e.getMessage());
      // Rethrow as a WebApplicationException to ensure a proper HTTP response
      throw new WebApplicationException(
          "Failed to create reservation due to a server error.",
          Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Validates if a reservation object is valid.
   *
   * @param reservation the reservation to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidReservation(Reservation reservation) {
    return reservation != null && reservation.user != null && reservation.book != null;
  }

  /**
   * Validates if a reservation ID is valid.
   *
   * @param id the reservation ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidReservationId(Long id) {
    return id != null && id > 0;
  }

  /**
   * Validates if a user ID is valid.
   *
   * @param userId the user ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidUserId(Long userId) {
    return userId != null && userId > 0;
  }

  /**
   * Validates if a book ID is valid.
   *
   * @param bookId the book ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBookId(Long bookId) {
    return bookId != null && bookId > 0;
  }

  /**
   * Checks whether a user already has an active reservation for a given book.
   *
   * @param userId the user ID
   * @param bookId the book ID
   * @return true if there is an active reservation by the user for the book
   */
  public boolean hasActiveReservationForBook(Long userId, Long bookId) {
    if (!isValidUserId(userId) || !isValidBookId(bookId)) {
      throw new IllegalArgumentException("Invalid user or book ID provided.");
    }
    return reservationRepository.hasActiveReservationForBook(userId, bookId);
  }

  public List<Reservation> getReservationsByUserId(Long userId) {
    if (!isValidUserId(userId)) {
      throw new IllegalArgumentException("Invalid user ID provided.");
    }
    return reservationRepository.findByUser(userId);
  }

  public long countActiveReservationsByBook(Long bookId) {
    if (!isValidBookId(bookId)) {
      throw new IllegalArgumentException("Invalid book ID provided.");
    }
    return reservationRepository.count(
        "book.id = ?1 and status = ?2", bookId, Reservation.ReservationStatus.ACTIVE);
  }

  public Optional<Integer> getQueuePosition(Long userId, Long bookId) {
    if (!isValidUserId(userId) || !isValidBookId(bookId)) {
      throw new IllegalArgumentException("Invalid user or book ID provided.");
    }
    return reservationRepository.getQueuePosition(userId, bookId);
  }

  @Transactional
  public List<ReservationDTO> getReservationsWithQueuePosition(Long userId) {
    if (!isValidUserId(userId)) {
      throw new IllegalArgumentException("Invalid user ID provided.");
    }

    List<Reservation> reservations = reservationRepository.findByUser(userId);

    return reservations.stream()
        .map(
            reservation -> {
              Integer queuePosition =
                  reservationRepository.getQueuePosition(userId, reservation.book.id).orElse(null);
              return new ReservationDTO(
                  reservation.id,
                  reservation.book.id,
                  reservation.book.title,
                  reservation.reservationDate.toLocalDate(),
                  reservation.status.name(),
                  queuePosition);
            })
        .collect(Collectors.toList());
  }

  @Transactional
  public Reservation cancelReservation(Long reservationId) {
    if (!isValidReservationId(reservationId)) {
      throw new IllegalArgumentException("Invalid reservation ID provided.");
    }
    Reservation reservation = reservationRepository.findById(reservationId);
    if (reservation == null) {
      throw new WebApplicationException("Reservation not found", Response.Status.NOT_FOUND);
    }
    reservation.status = Reservation.ReservationStatus.CANCELLED;
    reservationRepository.persist(reservation);
    return reservation;
  }
}
