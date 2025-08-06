package com.davonlibrary.service;

import com.davonlibrary.entity.Author;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthorService {

  /**
   * Validates if an author object is valid.
   *
   * @param author the author to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidAuthor(Author author) {
    return author != null
        && author.firstName != null
        && !author.firstName.trim().isEmpty()
        && author.lastName != null
        && !author.lastName.trim().isEmpty();
  }

  /**
   * Validates if an author ID is valid.
   *
   * @param id the author ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidAuthorId(Long id) {
    return id != null && id > 0;
  }

  /**
   * Validates if a name search term is valid.
   *
   * @param name the name to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidNameSearch(String name) {
    return name != null && !name.trim().isEmpty() && name.length() >= 2;
  }

  /**
   * Validates if first and last names are valid.
   *
   * @param firstName the first name to validate
   * @param lastName the last name to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidAuthorNames(String firstName, String lastName) {
    return firstName != null
        && !firstName.trim().isEmpty()
        && lastName != null
        && !lastName.trim().isEmpty();
  }
}
