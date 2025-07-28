package com.davonlibrary.repository;

import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.BookCopy.BookCopyStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/** Repository for BookCopy entity operations and queries. */
@ApplicationScoped
public class BookCopyRepository implements PanacheRepository<BookCopy> {

  /**
   * Finds a book copy by barcode.
   *
   * @param barcode the barcode
   * @return the book copy if found
   */
  public Optional<BookCopy> findByBarcode(String barcode) {
    return find("barcode", barcode).firstResultOptional();
  }

  /**
   * Finds all available book copies.
   *
   * @return list of available book copies
   */
  public List<BookCopy> findAvailable() {
    return list("isAvailable = true");
  }

  /**
   * Finds book copies by library.
   *
   * @param libraryId the library ID
   * @return list of book copies in the library
   */
  public List<BookCopy> findByLibrary(Long libraryId) {
    return list("library.id", libraryId);
  }

  /**
   * Finds available book copies by library.
   *
   * @param libraryId the library ID
   * @return list of available book copies in the library
   */
  public List<BookCopy> findAvailableByLibrary(Long libraryId) {
    return list("library.id = ?1 AND isAvailable = true", libraryId);
  }

  /**
   * Finds book copies by book.
   *
   * @param bookId the book ID
   * @return list of book copies for the book
   */
  public List<BookCopy> findByBook(Long bookId) {
    return list("book.id", bookId);
  }

  /**
   * Finds available book copies by book.
   *
   * @param bookId the book ID
   * @return list of available book copies for the book
   */
  public List<BookCopy> findAvailableByBook(Long bookId) {
    return list("book.id = ?1 AND isAvailable = true", bookId);
  }

  /**
   * Finds book copies by status.
   *
   * @param status the book copy status
   * @return list of book copies with the specified status
   */
  public List<BookCopy> findByStatus(BookCopyStatus status) {
    return list("status", status);
  }

  /**
   * Finds book copies by location.
   *
   * @param location the location
   * @return list of book copies in the specified location
   */
  public List<BookCopy> findByLocation(String location) {
    return list("LOWER(location) = LOWER(?1)", location);
  }

  /**
   * Finds book copies in maintenance.
   *
   * @return list of book copies in maintenance
   */
  public List<BookCopy> findInMaintenance() {
    return list("status = ?1", BookCopyStatus.MAINTENANCE);
  }

  /**
   * Finds damaged book copies.
   *
   * @return list of damaged book copies
   */
  public List<BookCopy> findDamaged() {
    return list("status = ?1", BookCopyStatus.DAMAGED);
  }

  /**
   * Finds lost book copies.
   *
   * @return list of lost book copies
   */
  public List<BookCopy> findLost() {
    return list("status = ?1", BookCopyStatus.LOST);
  }

  /**
   * Counts book copies by library.
   *
   * @param libraryId the library ID
   * @return number of book copies in the library
   */
  public long countByLibrary(Long libraryId) {
    return count("library.id", libraryId);
  }

  /**
   * Counts available book copies by library.
   *
   * @param libraryId the library ID
   * @return number of available book copies in the library
   */
  public long countAvailableByLibrary(Long libraryId) {
    return count("library.id = ?1 AND isAvailable = true", libraryId);
  }

  /**
   * Counts book copies by book.
   *
   * @param bookId the book ID
   * @return number of book copies for the book
   */
  public long countByBook(Long bookId) {
    return count("book.id", bookId);
  }

  /**
   * Counts available book copies by book.
   *
   * @param bookId the book ID
   * @return number of available book copies for the book
   */
  public long countAvailableByBook(Long bookId) {
    return count("book.id = ?1 AND isAvailable = true", bookId);
  }

  /**
   * Checks if a book copy exists by barcode.
   *
   * @param barcode the barcode
   * @return true if book copy exists
   */
  public boolean existsByBarcode(String barcode) {
    return count("barcode", barcode) > 0;
  }

  /**
   * Gets book copy statistics for a library.
   *
   * @param libraryId the library ID
   * @return book copy statistics
   */
  public BookCopyStats getBookCopyStats(Long libraryId) {
    long totalCopies = count("library.id", libraryId);
    long availableCopies = count("library.id = ?1 AND isAvailable = true", libraryId);
    long checkedOutCopies =
        count("library.id = ?1 AND status = ?2", libraryId, BookCopyStatus.CHECKED_OUT);
    long maintenanceCopies =
        count("library.id = ?1 AND status = ?2", libraryId, BookCopyStatus.MAINTENANCE);
    long damagedCopies =
        count("library.id = ?1 AND status = ?2", libraryId, BookCopyStatus.DAMAGED);

    return new BookCopyStats(
        totalCopies, availableCopies, checkedOutCopies, maintenanceCopies, damagedCopies);
  }

  /** Book copy statistics DTO. */
  public static class BookCopyStats {
    public final long totalCopies;
    public final long availableCopies;
    public final long checkedOutCopies;
    public final long maintenanceCopies;
    public final long damagedCopies;

    public BookCopyStats(
        long totalCopies,
        long availableCopies,
        long checkedOutCopies,
        long maintenanceCopies,
        long damagedCopies) {
      this.totalCopies = totalCopies;
      this.availableCopies = availableCopies;
      this.checkedOutCopies = checkedOutCopies;
      this.maintenanceCopies = maintenanceCopies;
      this.damagedCopies = damagedCopies;
    }
  }
}
