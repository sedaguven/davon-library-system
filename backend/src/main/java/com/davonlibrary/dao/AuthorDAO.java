package com.davonlibrary.dao;

import com.davonlibrary.entity.Author;
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
 * Data Access Object for Author entity operations. Provides a clean abstraction layer over database
 * operations.
 */
@ApplicationScoped
public class AuthorDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthorDAO.class);

  @Inject EntityManager entityManager;

  /**
   * Find an author by ID.
   *
   * @param id the author ID
   * @return Optional containing the author if found
   */
  @Transactional
  public Optional<Author> findById(Long id) {
    try {
      Author author = entityManager.find(Author.class, id);
      return Optional.ofNullable(author);
    } catch (Exception e) {
      LOGGER.error("Error finding author by ID: {}", id, e);
      return Optional.empty();
    }
  }

  /**
   * Find an author by name (first or last name).
   *
   * @param name the name to search for
   * @return list of matching authors
   */
  @Transactional
  public List<Author> findByName(String name) {
    try {
      TypedQuery<Author> query =
          entityManager.createQuery(
              "SELECT a FROM Author a WHERE LOWER(a.firstName) LIKE LOWER(:name) "
                  + "OR LOWER(a.lastName) LIKE LOWER(:name) ORDER BY a.lastName, a.firstName",
              Author.class);
      query.setParameter("name", "%" + name + "%");
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding authors by name: {}", name, e);
      return List.of();
    }
  }

  /**
   * Find authors by first name.
   *
   * @param firstName the first name
   * @return list of authors with the first name
   */
  @Transactional
  public List<Author> findByFirstName(String firstName) {
    try {
      TypedQuery<Author> query =
          entityManager.createQuery(
              "SELECT a FROM Author a WHERE LOWER(a.firstName) LIKE LOWER(:firstName) "
                  + "ORDER BY a.lastName, a.firstName",
              Author.class);
      query.setParameter("firstName", "%" + firstName + "%");
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding authors by first name: {}", firstName, e);
      return List.of();
    }
  }

  /**
   * Find authors by last name.
   *
   * @param lastName the last name
   * @return list of authors with the last name
   */
  @Transactional
  public List<Author> findByLastName(String lastName) {
    try {
      TypedQuery<Author> query =
          entityManager.createQuery(
              "SELECT a FROM Author a WHERE LOWER(a.lastName) LIKE LOWER(:lastName) "
                  + "ORDER BY a.firstName",
              Author.class);
      query.setParameter("lastName", "%" + lastName + "%");
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding authors by last name: {}", lastName, e);
      return List.of();
    }
  }

  /**
   * Find the most popular authors (by number of books).
   *
   * @param limit the maximum number of results
   * @return list of popular authors
   */
  @Transactional
  public List<Author> findMostPopular(int limit) {
    try {
      TypedQuery<Author> query =
          entityManager.createQuery(
              "SELECT a FROM Author a LEFT JOIN a.books b "
                  + "GROUP BY a.id, a.firstName, a.lastName "
                  + "ORDER BY COUNT(b.id) DESC",
              Author.class);
      query.setMaxResults(limit);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding most popular authors", e);
      return List.of();
    }
  }

  /**
   * Find authors with no books.
   *
   * @return list of authors with no books
   */
  @Transactional
  public List<Author> findAuthorsWithNoBooks() {
    try {
      TypedQuery<Author> query =
          entityManager.createQuery(
              "SELECT a FROM Author a WHERE SIZE(a.books) = 0 "
                  + "ORDER BY a.lastName, a.firstName",
              Author.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding authors with no books", e);
      return List.of();
    }
  }

  /**
   * Save a new author.
   *
   * @param author the author to save
   * @return the saved author with generated ID
   */
  @Transactional
  public Author save(Author author) {
    try {
      entityManager.persist(author);
      entityManager.flush();
      LOGGER.info("Author saved successfully: {} {}", author.firstName, author.lastName);
      return author;
    } catch (Exception e) {
      LOGGER.error("Error saving author: {} {}", author.firstName, author.lastName, e);
      throw new RuntimeException("Failed to save author", e);
    }
  }

  /**
   * Update an existing author.
   *
   * @param author the author to update
   * @return the updated author
   */
  @Transactional
  public Author update(Author author) {
    try {
      Author updatedAuthor = entityManager.merge(author);
      LOGGER.info("Author updated successfully: {} {}", author.firstName, author.lastName);
      return updatedAuthor;
    } catch (Exception e) {
      LOGGER.error("Error updating author: {} {}", author.firstName, author.lastName, e);
      throw new RuntimeException("Failed to update author", e);
    }
  }

  /**
   * Delete an author by ID.
   *
   * @param id the author ID
   * @return true if deleted successfully
   */
  @Transactional
  public boolean deleteById(Long id) {
    try {
      Author author = entityManager.find(Author.class, id);
      if (author != null) {
        entityManager.remove(author);
        LOGGER.info("Author deleted successfully: {} {}", author.firstName, author.lastName);
        return true;
      }
      return false;
    } catch (Exception e) {
      LOGGER.error("Error deleting author with ID: {}", id, e);
      throw new RuntimeException("Failed to delete author", e);
    }
  }

  /**
   * Find all authors.
   *
   * @return list of all authors
   */
  @Transactional
  public List<Author> findAll() {
    try {
      TypedQuery<Author> query =
          entityManager.createQuery(
              "SELECT a FROM Author a ORDER BY a.lastName, a.firstName", Author.class);
      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error finding all authors", e);
      return List.of();
    }
  }

  /**
   * Count total number of authors.
   *
   * @return total count
   */
  @Transactional
  public long count() {
    try {
      TypedQuery<Long> query =
          entityManager.createQuery("SELECT COUNT(a) FROM Author a", Long.class);
      return query.getSingleResult();
    } catch (Exception e) {
      LOGGER.error("Error counting authors", e);
      return 0;
    }
  }

  /**
   * Get author statistics.
   *
   * @return array with [total authors, authors with books, authors without books, total books]
   */
  @Transactional
  public Object[] getAuthorStatistics() {
    try {
      // Total authors
      TypedQuery<Long> totalQuery =
          entityManager.createQuery("SELECT COUNT(a) FROM Author a", Long.class);
      long totalAuthors = totalQuery.getSingleResult();

      // Authors with books
      TypedQuery<Long> withBooksQuery =
          entityManager.createQuery(
              "SELECT COUNT(a) FROM Author a WHERE SIZE(a.books) > 0", Long.class);
      long authorsWithBooks = withBooksQuery.getSingleResult();

      // Authors without books
      TypedQuery<Long> withoutBooksQuery =
          entityManager.createQuery(
              "SELECT COUNT(a) FROM Author a WHERE SIZE(a.books) = 0", Long.class);
      long authorsWithoutBooks = withoutBooksQuery.getSingleResult();

      // Total books
      TypedQuery<Long> totalBooksQuery =
          entityManager.createQuery("SELECT COUNT(b) FROM Book b", Long.class);
      long totalBooks = totalBooksQuery.getSingleResult();

      return new Object[] {totalAuthors, authorsWithBooks, authorsWithoutBooks, totalBooks};
    } catch (Exception e) {
      LOGGER.error("Error getting author statistics", e);
      return new Object[] {0L, 0L, 0L, 0L};
    }
  }

  /**
   * Search authors by multiple criteria.
   *
   * @param firstName the first name search term
   * @param lastName the last name search term
   * @return list of matching authors
   */
  @Transactional
  public List<Author> searchAuthors(String firstName, String lastName) {
    try {
      StringBuilder jpql = new StringBuilder("SELECT a FROM Author a WHERE 1=1");

      if (firstName != null && !firstName.trim().isEmpty()) {
        jpql.append(" AND LOWER(a.firstName) LIKE LOWER(:firstName)");
      }
      if (lastName != null && !lastName.trim().isEmpty()) {
        jpql.append(" AND LOWER(a.lastName) LIKE LOWER(:lastName)");
      }

      jpql.append(" ORDER BY a.lastName, a.firstName");

      TypedQuery<Author> query = entityManager.createQuery(jpql.toString(), Author.class);

      if (firstName != null && !firstName.trim().isEmpty()) {
        query.setParameter("firstName", "%" + firstName + "%");
      }
      if (lastName != null && !lastName.trim().isEmpty()) {
        query.setParameter("lastName", "%" + lastName + "%");
      }

      return query.getResultList();
    } catch (Exception e) {
      LOGGER.error("Error searching authors", e);
      return List.of();
    }
  }
}
