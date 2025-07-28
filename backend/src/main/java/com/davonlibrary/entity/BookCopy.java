package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** BookCopy entity representing individual physical copies of books. */
@Entity
@Table(name = "book_copies")
public class BookCopy extends PanacheEntity {

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

  @Column(name = "is_available")
  public Boolean isAvailable = true;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public BookCopyStatus status = BookCopyStatus.AVAILABLE;

  @Size(max = 100, message = "Location must not exceed 100 characters")
  @Column(name = "location", length = 100)
  public String location;

  @Size(max = 500, message = "Notes must not exceed 500 characters")
  @Column(name = "notes", length = 500)
  public String notes;

  @JsonIgnore
  @OneToMany(mappedBy = "bookCopy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Loan> loans;

  /** Book copy status enumeration. */
  public enum BookCopyStatus {
    AVAILABLE,
    CHECKED_OUT,
    RESERVED,
    MAINTENANCE,
    LOST,
    DAMAGED
  }

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
  }

  /** Checks out this copy. */
  public void checkOut() {
    if (isAvailable) {
      isAvailable = false;
      // Update the book's available copies count
      if (book != null) {
        book.borrowCopy();
      }
    }
  }

  /** Returns this copy to available status. */
  public void returnCopy() {
    if (!isAvailable) {
      isAvailable = true;
      // Update the book's available copies count
      if (book != null) {
        book.returnCopy();
      }
    }
  }

  /** Marks this copy as damaged. */
  public void markAsDamaged() {
    isAvailable = false;
  }

  /**
   * Sends this copy to maintenance.
   *
   * @param reason the reason for maintenance
   */
  public void sendToMaintenance(String reason) {
    isAvailable = false;
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
        + ", isAvailable="
        + isAvailable
        + '}';
  }
}
