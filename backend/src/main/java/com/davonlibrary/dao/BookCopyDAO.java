package com.davonlibrary.dao;

import com.davonlibrary.entity.BookCopy;
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
 * Data Access Object for BookCopy entity operations. Provides a clean abstraction layer over
 * database operations.
 */
@ApplicationScoped
public class BookCopyDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(BookCopyDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a book copy by ID.
   *
   * @param id the book copy ID
   * @return Optional containing the book copy if found
   */
  @Transactional
  public Optional<BookCopy> findById(Long id) {
    try {
      BookCopy bookCopy = entityManager.find(BookCopy.class, id);
      return Optional.ofNullable(bookCopy);
    } catch (Exception e) {
      LOGGER.error("Error finding book copy by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find all copies of a book.
   *
   * @param bookId the book ID
   * @return list of book copies
   */
  @Transactional
  public List<BookCopy> findByBookId(Long bookId) {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc WHERE bc.book.id = :bookId ORDER BY bc.copyNumber",
              BookCopy.class);
      query.setParameter("bookId", bookId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding book copies by book ID: {}", bookId, e);
      return List.of();
    }
  }

  /**
   * Find available book copies (not currently loaned).
   *
   * @param bookId the book ID
   * @return list of available book copies
   */
  @Transactional
  public List<BookCopy> findAvailableByBookId(Long bookId) {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc WHERE bc.book.id = :bookId "
                  + "AND bc.status.statusName = 'AVAILABLE' ORDER BY bc.copyNumber",
              BookCopy.class);
      query.setParameter("bookId", bookId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding available book copies by book ID: {}", bookId, e);
      return List.of();
    }
  }

  /**
   * Find book copies by status.
   *
   * @param statusName the status name
   * @return list of book copies with the specified status
   */
  @Transactional
  public List<BookCopy> findByStatus(String statusName) {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc WHERE bc.status.statusName = :statusName "
                  + "ORDER BY bc.book.title, bc.copyNumber",
              BookCopy.class);
      query.setParameter("statusName", statusName);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding book copies by status: {}", statusName, e);
      return List.of();
    }
  }

  /**
   * Find book copies by condition.
   *
   * @param condition the condition
   * @return list of book copies with the specified condition
   */
  @Transactional
  public List<BookCopy> findByCondition(String condition) {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc WHERE bc.condition = :condition "
                  + "ORDER BY bc.book.title, bc.copyNumber",
              BookCopy.class);
      query.setParameter("condition", condition);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding book copies by condition: {}", condition, e);
      return List.of();
    }
  }

  /**
   * Find book copies that need maintenance.
   *
   * @return list of book copies that need maintenance
   */
  @Transactional
  public List<BookCopy> findNeedingMaintenance() {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc WHERE bc.condition IN ('POOR', 'DAMAGED') "
                  + "ORDER BY bc.book.title, bc.copyNumber",
              BookCopy.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding book copies needing maintenance", e);
      return List.of();
    }
  }

  /**
   * Find book copies by location.
   *
   * @param location the location
   * @return list of book copies in the specified location
   */
  @Transactional
  public List<BookCopy> findByLocation(String location) {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc WHERE bc.location = :location "
                  + "ORDER BY bc.book.title, bc.copyNumber",
              BookCopy.class);
      query.setParameter("location", location);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding book copies by location: {}", location, e);
      return List.of();
    }
  }

  /**
   * Save a new book copy.
   *
   * @param bookCopy the book copy to save
   * @return the saved book copy with generated ID
   */
  @Transactional
  public BookCopy save(BookCopy bookCopy) {
    try {
      entityManager.persist(bookCopy);
      entityManager.flush();
      LOGGER.info(
          "Book copy saved successfully: Book={}, Barcode={}",
          bookCopy.book.title,
          bookCopy.barcode);
      return bookCopy;
    } catch (Exception e) {
      LOGGER.error(
          "Error saving book copy: Book={}, Barcode={}", bookCopy.book.title, bookCopy.barcode, e);
      throw new RuntimeException("Failed to save book copy", e);
    }
  }

  /**
   * Update an existing book copy.
   *
   * @param bookCopy the book copy to update
   * @return the updated book copy
   */
  @Transactional
  public BookCopy update(BookCopy bookCopy) {
    try {
      BookCopy updatedBookCopy = entityManager.merge(bookCopy);
      LOGGER.info("Book copy updated successfully: ID={}", bookCopy.id);
      return updatedBookCopy;
    } catch (Exception e) {
      LOGGER.error("Error updating book copy: ID={}", bookCopy.id, e);
      throw new RuntimeException("Failed to update book copy", e);
    }
  }

  /**
   * Delete a book copy by ID.
   *
   * @param id the book copy ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      BookCopy bookCopy = entityManager.find(BookCopy.class, id);
      if (bookCopy != null) {
        entityManager.remove(bookCopy);
        LOGGER.info("Book copy deleted successfully: ID={}", id);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting book copy with ID: {}", id, e);
      throw new RuntimeException("Failed to delete book copy", e);
    }
  }

  /**
   * Find all book copies.
   *
   * @return list of all book copies
   */
  @Transactional
  public List<BookCopy> findAll() {
    try {
      TypedQuery<BookCopy> query =
          entityManager.createQuery(
              "SELECT bc FROM BookCopy bc ORDER BY bc.book.title, bc.copyNumber", BookCopy.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all book copies", e);
      return List.of();
    }
  }

  /**
   * Count total number of book copies.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(bc) FROM BookCopy bc", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting book copies", e);
      return 0;
    }
  }

  /**
   * Count available book copies for a book.
   *
   * @param bookId the book ID
   * @return number of available copies
   */
  @Transactional
  public long countAvailableByBookId(Long bookId) {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery(
              "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = :bookId "
                  + "AND bc.status.statusName = 'AVAILABLE'",
              Long.class);
      query.setParameter("bookId", bookId);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting available book copies for book: {}", bookId, e);
      return 0;
    }
  }

  /**
   * Get book copy statistics.
   *
   * @return array with [total copies, available copies, loaned copies, maintenance copies]
   */
  @Transactional
  public Object[] getBookCopyStatistics() {
    try {
      // Total copies
      TypedQuery<Long> totalQuery =
          entityManager.createQuery("SELECT COUNT(bc) FROM BookCopy bc", Long.class);
      long totalCopies = totalQuery.getSingleResult();

      // Available copies
      TypedQuery<Long> availableQuery =
          entityManager.createQuery(
              "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.status.statusName = 'AVAILABLE'",
              Long.class);
      long availableCopies = availableQuery.getSingleResult();

      // Loaned copies
      TypedQuery<Long> loanedQuery =
          entityManager.createQuery(
              "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.status.statusName = 'LOANED'",
              Long.class);
      long loanedCopies = loanedQuery.getSingleResult();

      // Maintenance copies
      TypedQuery<Long> maintenanceQuery =
          entityManager.createQuery(
              "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.status.statusName = 'MAINTENANCE'",
              Long.class);
      long maintenanceCopies = maintenanceQuery.getSingleResult();

      return new Object[] {totalCopies, availableCopies, loanedCopies, maintenanceCopies};
    } catch (Exception e) {
      LOGGER.error("Error getting book copy statistics", e);
      return new Object[] {0L, 0L, 0L, 0L};
    }
  }

  /**
   * Find book copies by multiple criteria.
   *
   * @param bookId the book ID
   * @param status the status
   * @param condition the condition
   * @return list of matching book copies
   */
  @Transactional
  public List<BookCopy> searchBookCopies(Long bookId, String status, String condition) {
    try {
      StringBuilder jpql = new StringBuilder("SELECT bc FROM BookCopy bc WHERE 1=1");

      if (bookId != null) {
        jpql.append(" AND bc.book.id = :bookId");
      }
      if (status != null && !status.trim().isEmpty()) {
        jpql.append(" AND bc.status.statusName = :status");
      }
      if (condition != null && !condition.trim().isEmpty()) {
        jpql.append(" AND bc.condition = :condition");
      }

      jpql.append(" ORDER BY bc.book.title, bc.copyNumber");

      TypedQuery<BookCopy> query = entityManager.createQuery(jpql.toString(), BookCopy.class);

      if (bookId != null) {
        query.setParameter("bookId", bookId);
      }
      if (status != null && !status.trim().isEmpty()) {
        query.setParameter("status", status);
      }
      if (condition != null && !condition.trim().isEmpty()) {
        query.setParameter("condition", condition);
      }

      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error searching book copies", e);
      return List.of();
    }
  }
}
