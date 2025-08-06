package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** BookCopy entity representing individual physical copies of books. */
@Entity
@Table(name = "book_copies")
public class BookCopy extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  public enum BookCopyStatus {
    AVAILABLE,
    CHECKED_OUT,
    MAINTENANCE,
    DAMAGED,
    LOST
  }

  @NotBlank(message = "Barcode is required")
  @Size(max = 100, message = "Barcode must not exceed 100 characters")
  @Column(name = "barcode", nullable = false, unique = true, length = 100)
  public String barcode;

  @NotNull(message = "Book is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  public Book book;

  @NotNull(message = "Library is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "library_id", nullable = false)
  public Library library;

  @NotNull(message = "Status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  public BookCopyStatus status;

  @Size(max = 100, message = "Location must not exceed 100 characters")
  @Column(name = "location", length = 100)
  public String location;

  @Size(max = 500, message = "Notes must not exceed 500 characters")
  @Column(name = "notes", length = 500)
  public String notes;

  @JsonIgnore
  @OneToMany(mappedBy = "bookCopy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Loan> loans;

  /** Default constructor for JPA. */
  public BookCopy() {}

  /**
   * Constructor with essential fields.
   *
   * @param book the book this copy represents
   * @param library the library housing this copy
   * @param barcode the unique barcode for this copy
   */
  public BookCopy(Book book, Library library, String barcode) {
    this.book = book;
    this.library = library;
    this.barcode = barcode;
    this.status = BookCopyStatus.AVAILABLE;
  }

  /**
   * Constructor with location.
   *
   * @param book the book this copy represents
   * @param library the library housing this copy
   * @param barcode the unique barcode for this copy
   * @param location the shelf location
   */
  public BookCopy(Book book, Library library, String barcode, String location) {
    this.book = book;
    this.library = library;
    this.barcode = barcode;
    this.location = location;
    this.status = BookCopyStatus.AVAILABLE;
  }

  /**
   * Checks if this copy is available.
   *
   * @return true if the status is AVAILABLE
   */
  public boolean isAvailable() {
    return this.status == BookCopyStatus.AVAILABLE;
  }

  /** Checks out this copy. */
  public void checkOut() {
    if (this.status == BookCopyStatus.AVAILABLE) {
      this.status = BookCopyStatus.CHECKED_OUT;
    }
  }

  /** Returns this copy to available status. */
  public void returnCopy() {
    if (this.status == BookCopyStatus.CHECKED_OUT) {
      this.status = BookCopyStatus.AVAILABLE;
    }
  }

  /** Marks this copy as damaged. */
  public void markAsDamaged() {
    this.status = BookCopyStatus.DAMAGED;
  }

  /**
   * Sends this copy to maintenance.
   *
   * @param reason the reason for maintenance
   */
  public void sendToMaintenance(String reason) {
    this.status = BookCopyStatus.MAINTENANCE;
    this.notes = reason;
  }

  /**
   * Gets the current active loan for this copy.
   *
   * @return the active loan if exists
   */
  public Loan getCurrentLoan() {
    if (loans == null) {
      return null;
    }
    return loans.stream().filter(loan -> loan.returnDate == null).findFirst().orElse(null);
  }

  /**
   * Checks if this copy is currently on loan.
   *
   * @return true if the copy has an active loan
   */
  public boolean isOnLoan() {
    return getCurrentLoan() != null;
  }

  @Override
  public String toString() {
    return "BookCopy{"
        + "id="
        + id
        + ", book="
        + (book != null ? book.title : "null")
        + ", barcode='"
        + barcode
        + '\''
        + ", status="
        + status
        + '}';
  }
}
