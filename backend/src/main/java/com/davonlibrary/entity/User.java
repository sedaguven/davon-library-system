package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/** User entity representing library members. */
@Entity
@Table(name = "users")
public class User extends PanacheEntity {

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name must not exceed 100 characters")
  @Column(name = "first_name", nullable = false, length = 100)
  public String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name must not exceed 100 characters")
  @Column(name = "last_name", nullable = false, length = 100)
  public String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  @Column(name = "email", nullable = false, unique = true)
  public String email;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<LibraryMembership> memberships;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Loan> loans;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Reservation> reservations;

  /** Default constructor for JPA. */
  public User() {}

  /**
   * Constructor with essential fields.
   *
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param email the user's email address
   */
  public User(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  /**
   * Constructor with library membership.
   *
   * @param firstName the user's first name
   * @param lastName the user's last name
   * @param email the user's email address
   * @param library the library this user wants to join
   */
  public User(String firstName, String lastName, String email, Library library) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    // Note: Library membership should be created separately
  }

  /**
   * Gets the full name of the user.
   *
   * @return the full name as "firstName lastName"
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Checks if the user can borrow more books.
   *
   * @return true if user can borrow more books
   */
  public boolean canBorrowBooks() {
    if (loans == null) {
      return true;
    }
    long currentLoans = getCurrentLoans().size();
    return currentLoans < 5; // Default limit
  }

  /**
   * Gets the current active loans for this user.
   *
   * @return list of active loans
   */
  public List<Loan> getCurrentLoans() {
    if (loans == null) {
      return List.of();
    }
    return loans.stream().filter(loan -> loan.returnDate == null).toList();
  }

  /**
   * Gets the reservation history for this user.
   *
   * @return list of reservations
   */
  public List<Reservation> getReservationHistory() {
    return reservations != null ? reservations : List.of();
  }

  /**
   * Gets the number of active loans for this user.
   *
   * @return number of active loans
   */
  public int getCurrentLoanCount() {
    return getCurrentLoans().size();
  }

  /**
   * Gets active reservations for this user.
   *
   * @return list of active reservations
   */
  public List<Reservation> getActiveReservations() {
    if (reservations == null) {
      return List.of();
    }
    return reservations.stream().filter(Reservation::isActive).toList();
  }

  /**
   * Gets all libraries this user is a member of.
   *
   * @return list of libraries
   */
  public List<Library> getLibraries() {
    if (memberships == null) {
      return List.of();
    }
    return memberships.stream()
        .filter(LibraryMembership::isActive)
        .map(membership -> membership.library)
        .toList();
  }

  /**
   * Gets active library memberships for this user.
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
   * Checks if user is a member of a specific library.
   *
   * @param library the library to check
   * @return true if user is an active member
   */
  public boolean isMemberOf(Library library) {
    if (memberships == null) {
      return false;
    }
    return memberships.stream()
        .anyMatch(membership -> membership.library.id.equals(library.id) && membership.isActive());
  }

  /**
   * Gets the membership for a specific library.
   *
   * @param library the library
   * @return the membership or null if not found
   */
  public LibraryMembership getMembershipFor(Library library) {
    if (memberships == null) {
      return null;
    }
    return memberships.stream()
        .filter(membership -> membership.library.id.equals(library.id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + '}';
  }
}
