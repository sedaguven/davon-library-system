package com.davonlibrary.repository;

import com.davonlibrary.entity.Book;
import com.davonlibrary.entity.BookCopy;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/** Repository for Book entity operations and queries. */
@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {

  /**
   * Finds a book by ISBN.
   *
   * @param isbn the ISBN
   * @return the book if found
   */
  public Optional<Book> findByIsbn(String isbn) {
    return find("isbn", isbn).firstResultOptional();
  }

  /**
   * Finds books by title containing search term.
   *
   * @param searchTerm the search term
   * @return list of books matching the search
   */
  public List<Book> findByTitleContaining(String searchTerm) {
    String pattern = "%" + searchTerm.toLowerCase() + "%";
    return list("LOWER(title) LIKE ?1", pattern);
  }

  /**
   * Finds books by author.
   *
   * @param authorId the author ID
   * @return list of books by the author
   */
  public List<Book> findByAuthor(Long authorId) {
    return list("author.id", authorId);
  }

  /**
   * Finds books by author name.
   *
   * @param authorName the author name (first or last)
   * @return list of books by authors matching the name
   */
  public List<Book> findByAuthorName(String authorName) {
    String pattern = "%" + authorName.toLowerCase() + "%";
    return list("LOWER(author.firstName) LIKE ?1 OR LOWER(author.lastName) LIKE ?1", pattern);
  }

  /**
   * Finds all available books (with available copies > 0).
   *
   * @return list of available books
   */
  public List<Book> findAvailable() {
    return list("availableCopies > 0");
  }

  /**
   * Finds books that are currently out of stock.
   *
   * @return list of books with no available copies
   */
  public List<Book> findOutOfStock() {
    return list("availableCopies = 0 OR availableCopies IS NULL");
  }

  /**
   * Finds the most popular books (most borrowed).
   *
   * @param limit the maximum number of results
   * @return list of popular books
   */
  public List<Book> findMostPopular(int limit) {
    return find("SELECT b FROM Book b LEFT JOIN b.loans l GROUP BY b ORDER BY COUNT(l) DESC")
        .page(0, limit)
        .list();
  }

  /**
   * Finds recently added books.
   *
   * @param limit the maximum number of results
   * @return list of recently added books
   */
  public List<Book> findRecentlyAdded(int limit) {
    return find("ORDER BY id DESC").page(0, limit).list();
  }

  /**
   * Searches books by multiple criteria.
   *
   * @param searchTerm search term for title and author
   * @param authorId specific author ID (optional)
   * @param availableOnly whether to include only available books
   * @return list of books matching the criteria
   */
  public List<Book> searchBooks(String searchTerm, Long authorId, boolean availableOnly) {
    StringBuilder query = new StringBuilder("1=1");

    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
      String pattern = "%" + searchTerm.toLowerCase() + "%";
      query
          .append(" AND (LOWER(title) LIKE '")
          .append(pattern)
          .append("'")
          .append(" OR LOWER(author.firstName) LIKE '")
          .append(pattern)
          .append("'")
          .append(" OR LOWER(author.lastName) LIKE '")
          .append(pattern)
          .append("')");
    }

    if (authorId != null) {
      query.append(" AND author.id = ").append(authorId);
    }

    if (availableOnly) {
      query.append(" AND availableCopies > 0");
    }

    return list(query.toString());
  }

  /**
   * Gets all book copies for a book.
   *
   * @param bookId the book ID
   * @return list of book copies
   */
  public List<BookCopy> getBookCopies(Long bookId) {
    return BookCopy.list("book.id", bookId);
  }

  /**
   * Gets available book copies for a book.
   *
   * @param bookId the book ID
   * @return list of available book copies
   */
  public List<BookCopy> getAvailableBookCopies(Long bookId) {
    return BookCopy.list("book.id = ?1 AND isAvailable = true", bookId);
  }

  /**
   * Counts total copies of a book across all libraries.
   *
   * @param bookId the book ID
   * @return total number of copies
   */
  public long countTotalCopies(Long bookId) {
    return count("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = ?1", bookId);
  }

  /**
   * Counts available copies of a book across all libraries.
   *
   * @param bookId the book ID
   * @return number of available copies
   */
  public long countAvailableCopies(Long bookId) {
    return count(
        "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = ?1 AND bc.isAvailable = true",
        bookId);
  }

  /**
   * Gets books with low inventory (less than specified threshold).
   *
   * @param threshold the threshold for low inventory
   * @return list of books with low inventory
   */
  public List<Book> findLowInventory(int threshold) {
    return list("availableCopies <= ?1", threshold);
  }

  /**
   * Checks if a book exists by ISBN.
   *
   * @param isbn the ISBN
   * @return true if book exists
   */
  public boolean existsByIsbn(String isbn) {
    return count("isbn", isbn) > 0;
  }

  /**
   * Gets book statistics.
   *
   * @return book statistics
   */
  public BookStats getBookStats() {
    long totalBooks = count();
    long availableBooks = count("availableCopies > 0");
    long outOfStockBooks = count("availableCopies = 0 OR availableCopies IS NULL");

    return new BookStats(totalBooks, availableBooks, outOfStockBooks);
  }

  /** Book statistics DTO. */
  public static class BookStats {
    public final long totalBooks;
    public final long availableBooks;
    public final long outOfStockBooks;

    public BookStats(long totalBooks, long availableBooks, long outOfStockBooks) {
      this.totalBooks = totalBooks;
      this.availableBooks = availableBooks;
      this.outOfStockBooks = outOfStockBooks;
    }
  }

  public Book findByIdWithCopies(Long id) {
    return find("SELECT b FROM Book b LEFT JOIN FETCH b.bookCopies WHERE b.id = ?1", id)
        .firstResult();
  }
}
