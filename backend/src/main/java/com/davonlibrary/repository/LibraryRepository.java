package com.davonlibrary.repository;

import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.Library;
import com.davonlibrary.entity.Staff;
import com.davonlibrary.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/** Repository for Library entity operations and queries. */
@ApplicationScoped
public class LibraryRepository implements PanacheRepository<Library> {

  /**
   * Finds all libraries.
   *
   * @return list of all libraries
   */
  public List<Library> findActive() {
    return listAll();
  }

  /**
   * Finds libraries by city.
   *
   * @param city the city name
   * @return list of libraries in the city
   */
  public List<Library> findByCity(String city) {
    return list("LOWER(city) = LOWER(?1)", city);
  }

  /**
   * Finds libraries by name containing search term.
   *
   * @param searchTerm the search term
   * @return list of libraries matching the search
   */
  public List<Library> findByNameContaining(String searchTerm) {
    String pattern = "%" + searchTerm.toLowerCase() + "%";
    return list("LOWER(name) LIKE ?1", pattern);
  }

  /**
   * Finds all libraries (no longer filtering by open status).
   *
   * @return list of all libraries
   */
  public List<Library> findCurrentlyOpen() {
    return listAll();
  }

  /**
   * Gets all members of a library.
   *
   * @param libraryId the library ID
   * @return list of library members
   */
  public List<User> getMembers(Long libraryId) {
    return User.list("library.id", libraryId);
  }

  /**
   * Gets all staff of a library.
   *
   * @param libraryId the library ID
   * @return list of library staff
   */
  public List<Staff> getStaff(Long libraryId) {
    return Staff.list("library.id", libraryId);
  }

  /**
   * Gets active staff of a library.
   *
   * @param libraryId the library ID
   * @return list of active library staff
   */
  public List<Staff> getActiveStaff(Long libraryId) {
    return Staff.list("library.id = ?1 AND employmentStatus = 'ACTIVE'", libraryId);
  }

  /**
   * Gets all book copies in a library.
   *
   * @param libraryId the library ID
   * @return list of book copies in the library
   */
  public List<BookCopy> getBookCopies(Long libraryId) {
    return BookCopy.list("library.id", libraryId);
  }

  /**
   * Gets available book copies in a library.
   *
   * @param libraryId the library ID
   * @return list of available book copies
   */
  public List<BookCopy> getAvailableBookCopies(Long libraryId) {
    return BookCopy.list("library.id = ?1 AND isAvailable = true", libraryId);
  }

  /**
   * Counts total members in a library.
   *
   * @param libraryId the library ID
   * @return number of members
   */
  public long countMembers(Long libraryId) {
    return count("SELECT COUNT(u) FROM User u WHERE u.library.id = ?1", libraryId);
  }

  /**
   * Counts total staff in a library.
   *
   * @param libraryId the library ID
   * @return number of staff
   */
  public long countStaff(Long libraryId) {
    return count("SELECT COUNT(s) FROM Staff s WHERE s.library.id = ?1", libraryId);
  }

  /**
   * Counts total book copies in a library.
   *
   * @param libraryId the library ID
   * @return number of book copies
   */
  public long countBookCopies(Long libraryId) {
    return count("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.library.id = ?1", libraryId);
  }

  /**
   * Counts available book copies in a library.
   *
   * @param libraryId the library ID
   * @return number of available book copies
   */
  public long countAvailableBookCopies(Long libraryId) {
    return count(
        "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.library.id = ?1 AND bc.isAvailable = true",
        libraryId);
  }

  /**
   * Gets library statistics.
   *
   * @param libraryId the library ID
   * @return library statistics
   */
  public LibraryStats getLibraryStats(Long libraryId) {
    long totalMembers = countMembers(libraryId);
    long totalStaff = countStaff(libraryId);
    long totalBookCopies = countBookCopies(libraryId);
    long availableBookCopies = countAvailableBookCopies(libraryId);

    return new LibraryStats(totalMembers, totalStaff, totalBookCopies, availableBookCopies);
  }

  /**
   * Finds the main library branch.
   *
   * @return the main library if exists
   */
  public Optional<Library> findMainBranch() {
    return find("id = 1").firstResultOptional(); // Assumes first library is main
  }

  /** Library statistics DTO. */
  public static class LibraryStats {
    public final long totalMembers;
    public final long totalStaff;
    public final long totalBookCopies;
    public final long availableBookCopies;

    public LibraryStats(
        long totalMembers, long totalStaff, long totalBookCopies, long availableBookCopies) {
      this.totalMembers = totalMembers;
      this.totalStaff = totalStaff;
      this.totalBookCopies = totalBookCopies;
      this.availableBookCopies = availableBookCopies;
    }
  }
}
