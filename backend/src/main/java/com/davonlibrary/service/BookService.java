package com.davonlibrary.service;

import com.davonlibrary.entity.Book;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookService {

  /**
   * Validates if a book object is valid.
   *
   * @param book the book to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBook(Book book) {
    return book != null
        && book.title != null
        && !book.title.trim().isEmpty()
        && book.isbn != null
        && !book.isbn.trim().isEmpty();
  }

  /**
   * Validates if a book ID is valid.
   *
   * @param id the book ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBookId(Long id) {
    return id != null && id > 0;
  }

  /**
   * Validates if an ISBN is valid.
   *
   * @param isbn the ISBN to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidIsbn(String isbn) {
    return isbn != null && !isbn.trim().isEmpty() && isbn.length() >= 10;
  }

  /**
   * Validates if a title search term is valid.
   *
   * @param title the title to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidTitleSearch(String title) {
    return title != null && !title.trim().isEmpty() && title.length() >= 2;
  }

  /**
   * Validates if an author ID is valid.
   *
   * @param authorId the author ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidAuthorId(Long authorId) {
    return authorId != null && authorId > 0;
  }

  /**
   * Validates book copy counts.
   *
   * @param totalCopies total number of copies
   * @param availableCopies available number of copies
   * @return true if valid, false otherwise
   */
  public boolean isValidCopyCounts(Integer totalCopies, Integer availableCopies) {
    if (totalCopies == null || totalCopies < 0) {
      return false;
    }
    if (availableCopies == null || availableCopies < 0) {
      return false;
    }
    return availableCopies <= totalCopies;
  }
}
