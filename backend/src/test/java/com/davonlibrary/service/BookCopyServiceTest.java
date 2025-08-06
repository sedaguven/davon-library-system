package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.Book;
import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.Library;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BookCopyServiceTest {

  @Inject BookCopyService bookCopyService;

  @Test
  void testServiceInjection() {
    assertNotNull(bookCopyService);
  }

  @Test
  void testIsValidBookCopy_WithValidBookCopy() {
    BookCopy bookCopy = new BookCopy();
    bookCopy.book = new Book();
    bookCopy.library = new Library();

    assertTrue(bookCopyService.isValidBookCopy(bookCopy));
  }

  @Test
  void testIsValidBookCopy_WithNullBookCopy() {
    assertFalse(bookCopyService.isValidBookCopy(null));
  }

  @Test
  void testIsValidBookCopy_WithNullBook() {
    BookCopy bookCopy = new BookCopy();
    bookCopy.book = null;
    bookCopy.library = new Library();

    assertFalse(bookCopyService.isValidBookCopy(bookCopy));
  }

  @Test
  void testIsValidBookCopy_WithNullLibrary() {
    BookCopy bookCopy = new BookCopy();
    bookCopy.book = new Book();
    bookCopy.library = null;

    assertFalse(bookCopyService.isValidBookCopy(bookCopy));
  }

  @Test
  void testIsValidBookCopyId_WithValidId() {
    assertTrue(bookCopyService.isValidBookCopyId(1L));
    assertTrue(bookCopyService.isValidBookCopyId(100L));
  }

  @Test
  void testIsValidBookCopyId_WithNullId() {
    assertFalse(bookCopyService.isValidBookCopyId(null));
  }

  @Test
  void testIsValidBookCopyId_WithZeroId() {
    assertFalse(bookCopyService.isValidBookCopyId(0L));
  }

  @Test
  void testIsValidBookCopyId_WithNegativeId() {
    assertFalse(bookCopyService.isValidBookCopyId(-1L));
  }

  @Test
  void testIsValidBookId_WithValidId() {
    assertTrue(bookCopyService.isValidBookId(1L));
    assertTrue(bookCopyService.isValidBookId(100L));
  }

  @Test
  void testIsValidBookId_WithNullId() {
    assertFalse(bookCopyService.isValidBookId(null));
  }

  @Test
  void testIsValidBookId_WithZeroId() {
    assertFalse(bookCopyService.isValidBookId(0L));
  }

  @Test
  void testIsValidBookId_WithNegativeId() {
    assertFalse(bookCopyService.isValidBookId(-1L));
  }

  @Test
  void testIsValidLibraryId_WithValidId() {
    assertTrue(bookCopyService.isValidLibraryId(1L));
    assertTrue(bookCopyService.isValidLibraryId(100L));
  }

  @Test
  void testIsValidLibraryId_WithNullId() {
    assertFalse(bookCopyService.isValidLibraryId(null));
  }

  @Test
  void testIsValidLibraryId_WithZeroId() {
    assertFalse(bookCopyService.isValidLibraryId(0L));
  }

  @Test
  void testIsValidLibraryId_WithNegativeId() {
    assertFalse(bookCopyService.isValidLibraryId(-1L));
  }
}
