package com.davonlibrary.repository;

import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.Reservation;
import com.davonlibrary.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Repository for User entity operations and queries. */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

  /**
   * Finds a user by email address.
   *
   * @param email the email address
   * @return the user if found
   */
  public Optional<User> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  /**
   * Finds all users belonging to a specific library.
   *
   * @param libraryId the library ID
   * @return list of users in the library
   */
  public List<User> findByLibrary(Long libraryId) {
    return list("library.id", libraryId);
  }

  /**
   * Finds all active users with valid registrations.
   *
   * @return list of active users
   */
  public List<User> findActiveUsers() {
    return list("registrationDate IS NOT NULL");
  }

  /**
   * Finds users who have active loans.
   *
   * @return list of users with active loans
   */
  public List<User> findUsersWithActiveLoans() {
    return list("SELECT DISTINCT u FROM User u JOIN u.loans l WHERE l.returnDate IS NULL");
  }

  /**
   * Finds users who have overdue books.
   *
   * @return list of users with overdue books
   */
  public List<User> findUsersWithOverdueBooks() {
    return list(
        "SELECT DISTINCT u FROM User u JOIN u.loans l WHERE l.returnDate IS NULL AND l.dueDate < CURRENT_DATE");
  }

  /**
   * Finds users registered within a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of users registered in the date range
   */
  public List<User> findUsersRegisteredBetween(LocalDateTime startDate, LocalDateTime endDate) {
    return list("registrationDate >= ?1 AND registrationDate <= ?2", startDate, endDate);
  }

  /**
   * Gets the current active loans for a user.
   *
   * @param userId the user ID
   * @return list of active loans
   */
  public List<Loan> getCurrentLoans(Long userId) {
    return Loan.list("user.id = ?1 AND returnDate IS NULL", userId);
  }

  /**
   * Gets the active reservations for a user.
   *
   * @param userId the user ID
   * @return list of active reservations
   */
  public List<Reservation> getActiveReservations(Long userId) {
    return Reservation.list("user.id = ?1 AND status = 'ACTIVE'", userId);
  }

  /**
   * Counts users by library.
   *
   * @param libraryId the library ID
   * @return number of users in the library
   */
  public long countByLibrary(Long libraryId) {
    return count("library.id", libraryId);
  }

  /**
   * Checks if a user exists by email.
   *
   * @param email the email address
   * @return true if user exists
   */
  public boolean existsByEmail(String email) {
    return count("email", email) > 0;
  }

  /**
   * Finds users by name (first or last name contains the search term).
   *
   * @param searchTerm the search term
   * @return list of users matching the search
   */
  public List<User> findByNameContaining(String searchTerm) {
    String pattern = "%" + searchTerm.toLowerCase() + "%";
    return list("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1", pattern);
  }

  /**
   * Gets user statistics for a library.
   *
   * @param libraryId the library ID
   * @return user statistics
   */
  public UserStats getUserStats(Long libraryId) {
    long totalUsers = count("library.id", libraryId);
    long activeLoans =
        count(
            "SELECT COUNT(DISTINCT u.id) FROM User u JOIN u.loans l WHERE u.library.id = ?1 AND l.returnDate IS NULL",
            libraryId);
    long activeReservations =
        count(
            "SELECT COUNT(DISTINCT u.id) FROM User u JOIN u.reservations r WHERE u.library.id = ?1 AND r.status = 'ACTIVE'",
            libraryId);

    return new UserStats(totalUsers, activeLoans, activeReservations);
  }

  /** User statistics DTO. */
  public static class UserStats {
    public final long totalUsers;
    public final long usersWithActiveLoans;
    public final long usersWithActiveReservations;

    public UserStats(long totalUsers, long usersWithActiveLoans, long usersWithActiveReservations) {
      this.totalUsers = totalUsers;
      this.usersWithActiveLoans = usersWithActiveLoans;
      this.usersWithActiveReservations = usersWithActiveReservations;
    }
  }
}
