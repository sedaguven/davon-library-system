package com.davonlibrary.dao;

import com.davonlibrary.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object for User entity operations. Provides a clean abstraction layer over database
 * operations.
 */
@ApplicationScoped
public class UserDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a user by ID.
   *
   * @param id the user ID
   * @return Optional containing the user if found
   */
  @Transactional
  public Optional<User> findById(Long id) {
    try {
      User user = entityManager.find(User.class, id);
      return Optional.ofNullable(user);
    } catch (Exception e) {
      LOGGER.error("Error finding user by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find a user by email.
   *
   * @param email the email address
   * @return Optional containing the user if found
   */
  @Transactional
  public Optional<User> findByEmail(String email) {
    try {
      TypedQuery<User> query =
          entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
      query.setParameter("email", email);
      return query.getResultList().stream().findFirst();
    } catch (Exception e) {
      LOGGER.error("Error finding user by email: {}", email, e);
      return Optional.empty();
    }
  }

  /**
   * Find users by name (first or last name).
   *
   * @param name the name to search for
   * @return list of matching users
   */
  @Transactional
  public List<User> findByName(String name) {
    try {
      TypedQuery<User> query =
          entityManager.createQuery(
              "SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(:name) "
                  + "OR LOWER(u.lastName) LIKE LOWER(:name)",
              User.class);
      query.setParameter("name", "%" + name + "%");
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding users by name: {}", name, e);
      return List.of();
    }
  }

  /**
   * Find users with outstanding fines.
   *
   * @return list of users with unpaid fines
   */
  @Transactional
  public List<User> findUsersWithOutstandingFines() {
    try {
      TypedQuery<User> query =
          entityManager.createQuery(
              "SELECT DISTINCT u FROM User u "
                  + "JOIN u.fines f "
                  + "WHERE f.isPaid = false "
                  + "AND f.status.statusName = 'ACTIVE'",
              User.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding users with outstanding fines", e);
      return List.of();
    }
  }

  /**
   * Find users with overdue loans.
   *
   * @return list of users with overdue books
   */
  @Transactional
  public List<User> findUsersWithOverdueLoans() {
    try {
      TypedQuery<User> query =
          entityManager.createQuery(
              "SELECT DISTINCT u FROM User u "
                  + "JOIN u.loans l "
                  + "WHERE l.dueDate < CURRENT_DATE "
                  + "AND l.returnDate IS NULL "
                  + "AND l.status.statusName = 'ACTIVE'",
              User.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding users with overdue loans", e);
      return List.of();
    }
  }

  /**
   * Find active users (with recent activity).
   *
   * @param daysActive number of days to consider as "recent"
   * @return list of active users
   */
  @Transactional
  public List<User> findActiveUsers(int daysActive) {
    try {
      TypedQuery<User> query =
          entityManager.createQuery(
              "SELECT DISTINCT u FROM User u "
                  + "JOIN u.loans l "
                  + "WHERE l.loanDate >= DATEADD(day, -:daysActive, CURRENT_DATE)",
              User.class);
      query.setParameter("daysActive", daysActive);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding active users", e);
      return List.of();
    }
  }

  /**
   * Find users by library membership.
   *
   * @param libraryId the library ID
   * @return list of users with membership at the library
   */
  @Transactional
  public List<User> findByLibraryMembership(Long libraryId) {
    try {
      TypedQuery<User> query =
          entityManager.createQuery(
              "SELECT u FROM User u "
                  + "JOIN u.memberships m "
                  + "WHERE m.library.id = :libraryId "
                  + "AND m.status.statusName = 'ACTIVE'",
              User.class);
      query.setParameter("libraryId", libraryId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding users by library membership: {}", libraryId, e);
      return List.of();
    }
  }

  /**
   * Save a new user.
   *
   * @param user the user to save
   * @return the saved user with generated ID
   */
  @Transactional
  public User save(User user) {
    try {
      entityManager.persist(user);
      entityManager.flush();
      LOGGER.info("User saved successfully: {} {}", user.firstName, user.lastName);
      return user;
    } catch (Exception e) {
      LOGGER.error("Error saving user: {} {}", user.firstName, user.lastName, e);
      throw new RuntimeException("Failed to save user", e);
    }
  }

  /**
   * Update an existing user.
   *
   * @param user the user to update
   * @return the updated user
   */
  @Transactional
  public User update(User user) {
    try {
      User updatedUser = entityManager.merge(user);
      LOGGER.info("User updated successfully: {} {}", user.firstName, user.lastName);
      return updatedUser;
    } catch (Exception e) {
      LOGGER.error("Error updating user: {} {}", user.firstName, user.lastName, e);
      throw new RuntimeException("Failed to update user", e);
    }
  }

  /**
   * Delete a user by ID.
   *
   * @param id the user ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      User user = entityManager.find(User.class, id);
      if (user != null) {
        entityManager.remove(user);
        LOGGER.info("User deleted successfully: {} {}", user.firstName, user.lastName);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting user with ID: {}", id, e);
      throw new RuntimeException("Failed to delete user", e);
    }
  }

  /**
   * Find all users.
   *
   * @return list of all users
   */
  @Transactional
  public List<User> findAll() {
    try {
      TypedQuery<User> query =
          entityManager.createQuery(
              "SELECT u FROM User u ORDER BY u.lastName, u.firstName", User.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all users", e);
      return List.of();
    }
  }

  /**
   * Count total number of users.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting users", e);
      return 0;
    }
  }

  /**
   * Check if a user can borrow more books.
   *
   * @param userId the user ID
   * @param maxLoans maximum allowed loans
   * @return true if user can borrow more books
   */
  @Transactional
  public boolean canUserBorrowBooks(Long userId, int maxLoans) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l "
                  + "WHERE l.user.id = :userId "
                  + "AND l.returnDate IS NULL "
                  + "AND l.status.statusName = 'ACTIVE'",
              Long.class);
      query.setParameter("userId", userId);
      long currentLoans = query.getSingleResult();
      return currentLoans < maxLoans;
    } catch (Exception e) {
      LOGGER.error("Error checking if user can borrow books: {}", userId, e);
      return false;
    }
  }

  /**
   * Get user's borrowing statistics.
   *
   * @param userId the user ID
   * @return array with [total loans, current loans, overdue loans, total fines]
   */
  @Transactional
  public Object[] getUserBorrowingStats(Long userId) {
    try {
      // Total loans
      TypedQuery<Long> totalQuery =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId", Long.class);
      totalQuery.setParameter("userId", userId);
      long totalLoans = totalQuery.getSingleResult();

      // Current loans
      TypedQuery<Long> currentQuery =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId "
                  + "AND l.returnDate IS NULL AND l.status.statusName = 'ACTIVE'",
              Long.class);
      currentQuery.setParameter("userId", userId);
      long currentLoans = currentQuery.getSingleResult();

      // Overdue loans
      TypedQuery<Long> overdueQuery =
          entityManager.createQuery(
              "SELECT COUNT(l) FROM Loan l WHERE l.user.id = :userId "
                  + "AND l.dueDate < CURRENT_DATE AND l.returnDate IS NULL "
                  + "AND l.status.statusName = 'ACTIVE'",
              Long.class);
      overdueQuery.setParameter("userId", userId);
      long overdueLoans = overdueQuery.getSingleResult();

      // Total fines
      TypedQuery<Double> finesQuery =
          entityManager.createQuery(
              "SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.user.id = :userId",
              Double.class);
      finesQuery.setParameter("userId", userId);
      double totalFines = finesQuery.getSingleResult();

      return new Object[] {totalLoans, currentLoans, overdueLoans, totalFines};
    } catch (Exception e) {
      LOGGER.error("Error getting user borrowing stats: {}", userId, e);
      return new Object[] {0L, 0L, 0L, 0.0};
    }
  }

  /**
   * Search users by multiple criteria.
   *
   * @param firstName the first name search term
   * @param lastName the last name search term
   * @param email the email search term
   * @return list of matching users
   */
  @Transactional
  public List<User> searchUsers(String firstName, String lastName, String email) {
    try {
      StringBuilder jpql = new StringBuilder("SELECT u FROM User u WHERE 1=1");

      if (firstName != null && !firstName.trim().isEmpty()) {
        jpql.append(" AND LOWER(u.firstName) LIKE LOWER(:firstName)");
      }
      if (lastName != null && !lastName.trim().isEmpty()) {
        jpql.append(" AND LOWER(u.lastName) LIKE LOWER(:lastName)");
      }
      if (email != null && !email.trim().isEmpty()) {
        jpql.append(" AND LOWER(u.email) LIKE LOWER(:email)");
      }

      jpql.append(" ORDER BY u.lastName, u.firstName");

      TypedQuery<User> query = entityManager.createQuery(jpql.toString(), User.class);

      if (firstName != null && !firstName.trim().isEmpty()) {
        query.setParameter("firstName", "%" + firstName + "%");
      }
      if (lastName != null && !lastName.trim().isEmpty()) {
        query.setParameter("lastName", "%" + lastName + "%");
      }
      if (email != null && !email.trim().isEmpty()) {
        query.setParameter("email", "%" + email + "%");
      }

      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error searching users", e);
      return List.of();
    }
  }
}
