package com.davonlibrary.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** LibraryMembership entity representing the many-to-many relationship between User and Library. */
@Entity
@Table(name = "library_memberships")
public class LibraryMembership extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @NotNull(message = "User is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  public User user;

  @NotNull(message = "Library is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id", nullable = false)
  public Library library;

  @NotNull(message = "Join date is required")
  @Column(name = "join_date", nullable = false)
  public LocalDate joinDate = LocalDate.now();

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public MembershipStatus status = MembershipStatus.ACTIVE;

  /** Membership status enumeration. */
  public enum MembershipStatus {
    ACTIVE,
    SUSPENDED,
    EXPIRED,
    CANCELLED
  }

  /** Default constructor for JPA. */
  public LibraryMembership() {}

  /**
   * Constructor with essential fields.
   *
   * @param user the user
   * @param library the library
   */
  public LibraryMembership(User user, Library library) {
    this.user = user;
    this.library = library;
  }

  /**
   * Constructor with join date.
   *
   * @param user the user
   * @param library the library
   * @param joinDate the join date
   */
  public LibraryMembership(User user, Library library, LocalDate joinDate) {
    this.user = user;
    this.library = library;
    this.joinDate = joinDate;
  }

  /**
   * Checks if the membership is currently active.
   *
   * @return true if membership status is active
   */
  public boolean isActive() {
    return status == MembershipStatus.ACTIVE;
  }

  /** Suspends the membership. */
  public void suspend() {
    this.status = MembershipStatus.SUSPENDED;
  }

  /** Activates the membership. */
  public void activate() {
    this.status = MembershipStatus.ACTIVE;
  }

  /** Cancels the membership. */
  public void cancel() {
    this.status = MembershipStatus.CANCELLED;
  }

  /**
   * Gets the number of years of membership.
   *
   * @return years of membership
   */
  public long getYearsOfMembership() {
    return joinDate.until(LocalDate.now()).getYears();
  }

  @Override
  public String toString() {
    return "LibraryMembership{"
        + "id="
        + id
        + ", user="
        + (user != null ? user.getFullName() : "null")
        + ", library="
        + (library != null ? library.name : "null")
        + ", joinDate="
        + joinDate
        + ", status="
        + status
        + '}';
  }
}
