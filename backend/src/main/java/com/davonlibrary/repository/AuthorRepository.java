package com.davonlibrary.repository;

import com.davonlibrary.entity.Author;
import com.davonlibrary.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/** Repository for Author entity operations and queries. */
@ApplicationScoped
public class AuthorRepository implements PanacheRepository<Author> {

  @Inject EntityManager em;

  /**
   * Finds authors by name containing search term.
   *
   * @param searchTerm the search term
   * @return list of authors matching the search
   */
  public List<Author> findByNameContaining(String searchTerm) {
    String pattern = "%" + searchTerm.toLowerCase() + "%";
    return list("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1", pattern);
  }

  /**
   * Finds an author by exact name match.
   *
   * @param firstName the first name
   * @param lastName the last name
   * @return the author if found
   */
  public Optional<Author> findByName(String firstName, String lastName) {
    return find("LOWER(firstName) = LOWER(?1) AND LOWER(lastName) = LOWER(?2)", firstName, lastName)
        .firstResultOptional();
  }

  /**
   * Finds authors by nationality.
   *
   * @param nationality the nationality
   * @return list of authors from the specified nationality
   */
  public List<Author> findByNationality(String nationality) {
    return list("LOWER(nationality) = LOWER(?1)", nationality);
  }

  /**
   * Gets all books written by an author.
   *
   * @param authorId the author ID
   * @return list of books by the author
   */
  public List<Book> getBooks(Long authorId) {
    return Book.list("author.id", authorId);
  }

  /**
   * Gets the most popular authors (by number of books).
   *
   * @param limit the maximum number of results
   * @return list of popular authors
   */
  @SuppressWarnings("unchecked")
  public List<Object[]> getMostPopularAuthors(int limit) {
    return em.createQuery(
            "SELECT a, COUNT(b) as bookCount FROM Author a LEFT JOIN a.books b GROUP BY a ORDER BY COUNT(b) DESC")
        .setMaxResults(limit)
        .getResultList();
  }

  /**
   * Counts books by an author.
   *
   * @param authorId the author ID
   * @return number of books by the author
   */
  public long countBooks(Long authorId) {
    return count("SELECT COUNT(b) FROM Book b WHERE b.author.id = ?1", authorId);
  }

  /**
   * Checks if an author has any books.
   *
   * @param authorId the author ID
   * @return true if the author has books
   */
  public boolean hasBooks(Long authorId) {
    return count("SELECT COUNT(b) FROM Book b WHERE b.author.id = ?1", authorId) > 0;
  }

  /**
   * Gets author statistics.
   *
   * @return author statistics
   */
  public AuthorStats getAuthorStats() {
    long totalAuthors = count();
    long authorsWithBooks = count("SELECT COUNT(DISTINCT a) FROM Author a JOIN a.books b");

    return new AuthorStats(totalAuthors, authorsWithBooks);
  }

  /** Author statistics DTO. */
  public static class AuthorStats {
    public final long totalAuthors;
    public final long authorsWithBooks;

    public AuthorStats(long totalAuthors, long authorsWithBooks) {
      this.totalAuthors = totalAuthors;
      this.authorsWithBooks = authorsWithBooks;
    }
  }
}
