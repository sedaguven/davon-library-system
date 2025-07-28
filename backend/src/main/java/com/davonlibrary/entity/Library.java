package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Comparator;
import java.util.List;

/** Library entity representing physical library locations. */
@Entity
@Table(name = "libraries")
public class Library extends PanacheEntity {

  @NotBlank(message = "Library name is required")
  @Size(max = 200, message = "Library name must not exceed 200 characters")
  @Column(name = "name", nullable = false, length = 200)
  public String name;

  @Size(max = 500, message = "Address must not exceed 500 characters")
  @Column(name = "address", length = 500)
  public String address;

  @Size(max = 100, message = "City must not exceed 100 characters")
  @Column(name = "city", length = 100)
  public String city;

  @JsonIgnore
  @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<BookCopy> bookCopies;

  @JsonIgnore
  @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<LibraryMembership> memberships;

  @JsonIgnore
  @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Staff> staff;

  /** Default constructor for JPA. */
  public Library() {}

  /**
   * Constructor with essential fields.
   *
   * @param name the library name
   * @param address the library address
   */
  public Library(String name, String address) {
    this.name = name;
    this.address = address;
  }

  /**
   * Constructor with main fields.
   *
   * @param name the library name
   * @param address the library address
   * @param city the city
   */
  public Library(String name, String address, String city) {
    this.name = name;
    this.address = address;
    this.city = city;
  }

  /**
   * Gets the full address as a formatted string.
   *
   * @return the formatted address
   */
  public String getFullAddress() {
    StringBuilder sb = new StringBuilder();
    if (address != null) {
      sb.append(address);
    }
    if (city != null) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(city);
    }
    return sb.toString();
  }

  /**
   * Gets available books in this library.
   *
   * @return list of available books
   */
  public List<Book> getAvailableBooks() {
    if (bookCopies == null) {
      return List.of();
    }

    return bookCopies.stream()
        .filter(bookCopy -> bookCopy.isAvailable != null && bookCopy.isAvailable)
        .map(bookCopy -> bookCopy.book)
        .distinct()
        .toList();
  }

  /**
   * Gets the number of registered members for this library.
   *
   * @return the number of members
   */
  public int getMemberCount() {
    if (memberships == null) {
      return 0;
    }
    return (int) memberships.stream().filter(LibraryMembership::isActive).count();
  }

  /**
   * Gets all active members of this library.
   *
   * @return list of active members
   */
  public List<User> getActiveMembers() {
    if (memberships == null) {
      return List.of();
    }
    return memberships.stream()
        .filter(LibraryMembership::isActive)
        .map(membership -> membership.user)
        .toList();
  }

  /**
   * Gets all active memberships for this library.
   *
   * @return list of active memberships
   */
  public List<LibraryMembership> getActiveMemberships() {
    if (memberships == null) {
      return List.of();
    }
    return memberships.stream().filter(LibraryMembership::isActive).toList();
  }

  /**
   * Checks if a user is an active member of this library.
   *
   * @param user the user to check
   * @return true if user is an active member
   */
  public boolean hasActiveMember(User user) {
    if (memberships == null) {
      return false;
    }
    return memberships.stream()
        .anyMatch(membership -> membership.user.id.equals(user.id) && membership.isActive());
  }

  /**
   * Gets the membership for a specific user.
   *
   * @param user the user
   * @return the membership or null if not found
   */
  public LibraryMembership getMembershipFor(User user) {
    if (memberships == null) {
      return null;
    }
    return memberships.stream()
        .filter(membership -> membership.user.id.equals(user.id))
        .findFirst()
        .orElse(null);
  }

  /**
   * Gets the number of book copies available in this library.
   *
   * @return the number of book copies
   */
  public int getBookCopyCount() {
    return bookCopies != null ? bookCopies.size() : 0;
  }

  /**
   * Gets the number of active borrowers in this library. An active borrower is a member who
   * currently has at least one loan.
   *
   * @return the number of active borrowers
   */
  public int getActiveBorrowerCount() {
    if (memberships == null) {
      return 0;
    }
    return (int)
        memberships.stream()
            .filter(LibraryMembership::isActive)
            .filter(membership -> membership.user.getCurrentLoanCount() > 0)
            .count();
  }

  /**
   * Gets all active borrowers of this library. An active borrower is a member who currently has at
   * least one loan.
   *
   * @return list of active borrowers
   */
  public List<User> getActiveBorrowers() {
    if (memberships == null) {
      return List.of();
    }
    return memberships.stream()
        .filter(LibraryMembership::isActive)
        .map(membership -> membership.user)
        .filter(user -> user.getCurrentLoanCount() > 0)
        .toList();
  }

  /**
   * Gets the library with the most active borrowers.
   *
   * @return the library with the most active borrowers, or null if no libraries exist
   */
  public static Library getLibraryWithMostActiveBorrowers() {
    List<Library> allLibraries = Library.listAll();
    if (allLibraries.isEmpty()) {
      return null;
    }
    return allLibraries.stream()
        .max(Comparator.comparing(Library::getActiveBorrowerCount))
        .orElse(null);
  }

  /**
   * Gets libraries ordered by active borrower count (descending).
   *
   * @return list of libraries ordered by active borrower count
   */
  public static List<Library> getLibrariesOrderedByActiveBorrowers() {
    List<Library> allLibraries = Library.listAll();
    return allLibraries.stream()
        .sorted(Comparator.comparing(Library::getActiveBorrowerCount).reversed())
        .toList();
  }

  /**
   * Gets libraries ordered by member count (descending).
   *
   * @return list of libraries ordered by member count
   */
  public static List<Library> getLibrariesOrderedByMemberCount() {
    List<Library> allLibraries = Library.listAll();
    return allLibraries.stream()
        .sorted(Comparator.comparing(Library::getMemberCount).reversed())
        .toList();
  }

  /**
   * Gets the number of staff members in this library.
   *
   * @return the number of staff members
   */
  public int getStaffCount() {
    return staff != null ? staff.size() : 0;
  }

  /**
   * Gets detailed information about the library.
   *
   * @return formatted library information
   */
  public String getDetailedInfo() {
    return String.format(
        "%s - %s, %s - %d members, %d book copies, %d staff",
        name,
        address != null ? address : "No address",
        city != null ? city : "No city",
        getMemberCount(),
        getBookCopyCount(),
        getStaffCount());
  }

  @Override
  public String toString() {
    return "Library{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", address='"
        + address
        + '\''
        + ", city='"
        + city
        + '\''
        + '}';
  }
}
