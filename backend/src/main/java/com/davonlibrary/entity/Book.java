package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Book entity representing books in the library system. */
@Entity
@Table(name = "books")
public class Book extends PanacheEntity {

  @NotBlank(message = "Title is required")
  @Size(max = 255, message = "Title must not exceed 255 characters")
  @Column(name = "title", nullable = false)
  public String title;

  @Pattern(
      regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$",
      message = "Invalid ISBN format")
  @Column(name = "isbn", unique = true, length = 20)
  public String isbn;

  @Column(name = "available_copies")
  public Integer availableCopies = 1;

  @Column(name = "total_copies")
  public Integer totalCopies = 1;

  @NotNull(message = "Author is required")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  public Author author;

  @JsonIgnore
  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<BookCopy> bookCopies;

  /** Default constructor for JPA. */
  public Book() {}

  /**
   * Constructor with essential fields.
   *
   * @param title the book title
   * @param author the book author
   */
  public Book(String title, Author author) {
    this.title = title;
    this.author = author;
  }

  /**
   * Constructor with common fields.
   *
   * @param title the book title
   * @param isbn the book ISBN
   * @param author the book author
   */
  public Book(String title, String isbn, Author author) {
    this.title = title;
    this.isbn = isbn;
    this.author = author;
  }

  /**
   * Constructor with total copies.
   *
   * @param title the book title
   * @param isbn the book ISBN
   * @param author the book author
   * @param totalCopies the total number of copies
   */
  public Book(String title, String isbn, Author author, Integer totalCopies) {
    this.title = title;
    this.isbn = isbn;
    this.author = author;
    this.totalCopies = totalCopies;
    this.availableCopies = totalCopies;
  }

  /**
   * Checks if the book is available for borrowing.
   *
   * @return true if available copies > 0
   */
  public boolean isAvailable() {
    return availableCopies != null && availableCopies > 0;
  }

  /**
   * Decrements available copies when a book is borrowed.
   *
   * @return true if successful, false if no copies available
   */
  public boolean borrowCopy() {
    if (isAvailable()) {
      availableCopies--;
      return true;
    }
    return false;
  }

  /** Increments available copies when a book is returned. */
  public void returnCopy() {
    if (availableCopies < totalCopies) {
      availableCopies++;
    }
  }

  /**
   * Gets detailed information about the book.
   *
   * @return formatted book information
   */
  public String getDetailedInfo() {
    return String.format(
        "%s by %s (ISBN: %s) - %d copies available",
        title,
        author != null ? author.getFullName() : "Unknown Author",
        isbn != null ? isbn : "No ISBN",
        availableCopies);
  }

  /**
   * Gets all copies of this book.
   *
   * @return list of all book copies
   */
  public List<BookCopy> getAllCopies() {
    if (bookCopies == null) {
      return List.of();
    }
    return bookCopies;
  }

  /**
   * Gets current active reservations for this book.
   *
   * @return list of current reservations
   */
  public List<Reservation> getCurrentReservations() {
    return List.of(); // Simplified - no reservations relationship
  }

  @Override
  public String toString() {
    return "Book{"
        + "id="
        + id
        + ", title='"
        + title
        + '\''
        + ", isbn='"
        + isbn
        + '\''
        + ", author="
        + (author != null ? author.getFullName() : "null")
        + ", availableCopies="
        + availableCopies
        + '}';
  }
}
