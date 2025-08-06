package com.davonlibrary.dao;

import com.davonlibrary.entity.Book;
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
 * Data Access Object for Book entity operations. Provides a clean abstraction layer over database
 * operations.
 */
@ApplicationScoped
public class BookDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(BookDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find a book by its ID.
   *
   * @param id the book ID
   * @return Optional containing the book if found
   */
  @Transactional
  public Optional<Book> findById(Long id) {
    try {
      Book book = entityManager.find(Book.class, id);
      return Optional.ofNullable(book);
    } catch (Exception e) {
      LOGGER.error("Error finding book by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find a book by ISBN.
   *
   * @param isbn the ISBN
   * @return Optional containing the book if found
   */
  @Transactional
  public Optional<Book> findByIsbn(String isbn) {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
      query.setParameter("isbn", isbn);
      return query.getResultList().stream().findFirst();
    } catch (Exception e) {
      LOGGER.error("Error finding book by ISBN: {}", isbn, e);
      return Optional.empty();
    }
  }

  /**
   * Find books by title containing search term.
   *
   * @param searchTerm the search term
   * @return list of matching books
   */
  @Transactional
  public List<Book> findByTitleContaining(String searchTerm) {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery(
              "SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:searchTerm)", Book.class);
      query.setParameter("searchTerm", "%" + searchTerm + "%");
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding books by title: {}", searchTerm, e);
      return List.of();
    }
  }

  /**
   * Find books by author.
   *
   * @param authorId the author ID
   * @return list of books by the author
   */
  @Transactional
  public List<Book> findByAuthor(Long authorId) {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery(
              "SELECT b FROM Book b WHERE b.author.id = :authorId", Book.class);
      query.setParameter("authorId", authorId);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding books by author: {}", authorId, e);
      return List.of();
    }
  }

  /**
   * Find books by author name.
   *
   * @param authorName the author name (first or last)
   * @return list of books by authors matching the name
   */
  @Transactional
  public List<Book> findByAuthorName(String authorName) {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery(
              "SELECT b FROM Book b JOIN b.author a "
                  + "WHERE LOWER(a.firstName) LIKE LOWER(:authorName) "
                  + "OR LOWER(a.lastName) LIKE LOWER(:authorName)",
              Book.class);
      query.setParameter("authorName", "%" + authorName + "%");
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding books by author name: {}", authorName, e);
      return List.of();
    }
  }

  /**
   * Find all available books (with available copies > 0).
   *
   * @return list of available books
   */
  @Transactional
  public List<Book> findAvailable() {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery("SELECT b FROM Book b WHERE b.availableCopies > 0", Book.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding available books", e);
      return List.of();
    }
  }

  /**
   * Find books that are currently out of stock.
   *
   * @return list of books with no available copies
   */
  @Transactional
  public List<Book> findOutOfStock() {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery(
              "SELECT b FROM Book b WHERE b.availableCopies = 0 OR b.availableCopies IS NULL",
              Book.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding out of stock books", e);
      return List.of();
    }
  }

  /**
   * Find the most popular books (most borrowed).
   *
   * @param limit the maximum number of results
   * @return list of popular books
   */
  @Transactional
  public List<Book> findMostPopular(int limit) {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery(
              "SELECT b FROM Book b "
                  + "LEFT JOIN b.bookCopies bc "
                  + "LEFT JOIN bc.loans l "
                  + "GROUP BY b.id, b.title, b.isbn, b.availableCopies, b.totalCopies "
                  + "ORDER BY COUNT(l.id) DESC",
              Book.class);
      query.setMaxResults(limit);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding most popular books", e);
      return List.of();
    }
  }

  /**
   * Save a new book.
   *
   * @param book the book to save
   * @return the saved book with generated ID
   */
  @Transactional
  public Book save(Book book) {
    try {
      entityManager.persist(book);
      entityManager.flush();
      LOGGER.info("Book saved successfully: {}", book.title);
      return book;
    } catch (Exception e) {
      LOGGER.error("Error saving book: {}", book.title, e);
      throw new RuntimeException("Failed to save book", e);
    }
  }

  /**
   * Update an existing book.
   *
   * @param book the book to update
   * @return the updated book
   */
  @Transactional
  public Book update(Book book) {
    try {
      Book updatedBook = entityManager.merge(book);
      LOGGER.info("Book updated successfully: {}", book.title);
      return updatedBook;
    } catch (Exception e) {
      LOGGER.error("Error updating book: {}", book.title, e);
      throw new RuntimeException("Failed to update book", e);
    }
  }

  /**
   * Delete a book by ID.
   *
   * @param id the book ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      Book book = entityManager.find(Book.class, id);
      if (book != null) {
        entityManager.remove(book);
        LOGGER.info("Book deleted successfully: {}", book.title);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting book with ID: {}", id, e);
      throw new RuntimeException("Failed to delete book", e);
    }
  }

  /**
   * Find all books.
   *
   * @return list of all books
   */
  @Transactional
  public List<Book> findAll() {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery("SELECT b FROM Book b ORDER BY b.title", Book.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all books", e);
      return List.of();
    }
  }

  /**
   * Count total number of books.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(b) FROM Book b", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting books", e);
      return 0;
    }
  }

  /**
   * Search books by multiple criteria.
   *
   * @param title the title search term
   * @param authorName the author name search term
   * @param isbn the ISBN search term
   * @return list of matching books
   */
  @Transactional
  public List<Book> searchBooks(String title, String authorName, String isbn) {
    try {
      StringBuilder jpql = new StringBuilder("SELECT b FROM Book b JOIN b.author a WHERE 1=1");

      if (title != null && !title.trim().isEmpty()) {
        jpql.append(" AND LOWER(b.title) LIKE LOWER(:title)");
      }
      if (authorName != null && !authorName.trim().isEmpty()) {
        jpql.append(
            " AND (LOWER(a.firstName) LIKE LOWER(:authorName) OR LOWER(a.lastName) LIKE LOWER(:authorName))");
      }
      if (isbn != null && !isbn.trim().isEmpty()) {
        jpql.append(" AND b.isbn LIKE :isbn");
      }

      jpql.append(" ORDER BY b.title");

      TypedQuery<Book> query = entityManager.createQuery(jpql.toString(), Book.class);

      if (title != null && !title.trim().isEmpty()) {
        query.setParameter("title", "%" + title + "%");
      }
      if (authorName != null && !authorName.trim().isEmpty()) {
        query.setParameter("authorName", "%" + authorName + "%");
      }
      if (isbn != null && !isbn.trim().isEmpty()) {
        query.setParameter("isbn", "%" + isbn + "%");
      }

      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error searching books", e);
      return List.of();
    }
  }
}
