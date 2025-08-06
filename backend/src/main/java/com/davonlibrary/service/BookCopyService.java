package com.davonlibrary.service;

import com.davonlibrary.entity.BookCopy;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookCopyService {

  /**
   * Validates if a book copy object is valid.
   *
   * @param bookCopy the book copy to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBookCopy(BookCopy bookCopy) {
    return bookCopy != null && bookCopy.book != null && bookCopy.library != null;
  }

  /**
   * Validates if a book copy ID is valid.
   *
   * @param id the book copy ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBookCopyId(Long id) {
    return id != null && id > 0;
  }

  /**
   * Validates if a book ID is valid.
   *
   * @param bookId the book ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBookId(Long bookId) {
    return bookId != null && bookId > 0;
  }

  /**
   * Validates if a library ID is valid.
   *
   * @param libraryId the library ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidLibraryId(Long libraryId) {
    return libraryId != null && libraryId > 0;
  }

  /**
   * Validates if a barcode is valid.
   *
   * @param barcode the barcode to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidBarcode(String barcode) {
    return barcode != null && !barcode.trim().isEmpty();
  }
}
