package com.davonlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Author entity representing book authors in the library system. */
@Entity
@Table(name = "authors")
public class Author extends PanacheEntity {

  @NotBlank(message = "First name is required")
  @Size(max = 100, message = "First name must not exceed 100 characters")
  @Column(name = "first_name", nullable = false, length = 100)
  public String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 100, message = "Last name must not exceed 100 characters")
  @Column(name = "last_name", nullable = false, length = 100)
  public String lastName;

  @JsonIgnore
  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  public List<Book> books;

  /** Default constructor for JPA. */
  public Author() {}

  /**
   * Constructor with essential fields.
   *
   * @param firstName the author's first name
   * @param lastName the author's last name
   */
  public Author(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  /**
   * Gets the full name of the author.
   *
   * @return the full name as "firstName lastName"
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Gets all books written by this author.
   *
   * @return list of books written by this author
   */
  public List<Book> getBooksWritten() {
    return books != null ? books : List.of();
  }

  @Override
  public String toString() {
    return "Author{"
        + "id="
        + id
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + '}';
  }
}
