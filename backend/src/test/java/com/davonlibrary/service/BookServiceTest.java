package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.Book;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BookServiceTest {

  @Inject BookService bookService;

  @Test
  void testServiceInjection() {
    assertNotNull(bookService);
  }

  @Test
  void testIsValidBook_WithValidBook() {
    Book book = new Book();
    book.title = "Test Book";
    book.isbn = "1234567890";

    assertTrue(bookService.isValidBook(book));
  }

  @Test
  void testIsValidBook_WithNullBook() {
    assertFalse(bookService.isValidBook(null));
  }

  @Test
  void testIsValidBook_WithNullTitle() {
    Book book = new Book();
    book.title = null;
    book.isbn = "1234567890";

    assertFalse(bookService.isValidBook(book));
  }

  @Test
  void testIsValidBook_WithEmptyTitle() {
    Book book = new Book();
    book.title = "";
    book.isbn = "1234567890";

    assertFalse(bookService.isValidBook(book));
  }

  @Test
  void testIsValidBook_WithWhitespaceTitle() {
    Book book = new Book();
    book.title = "   ";
    book.isbn = "1234567890";

    assertFalse(bookService.isValidBook(book));
  }

  @Test
  void testIsValidBook_WithNullIsbn() {
    Book book = new Book();
    book.title = "Test Book";
    book.isbn = null;

    assertFalse(bookService.isValidBook(book));
  }

  @Test
  void testIsValidBook_WithEmptyIsbn() {
    Book book = new Book();
    book.title = "Test Book";
    book.isbn = "";

    assertFalse(bookService.isValidBook(book));
  }

  @Test
  void testIsValidBookId_WithValidId() {
    assertTrue(bookService.isValidBookId(1L));
    assertTrue(bookService.isValidBookId(100L));
  }

  @Test
  void testIsValidBookId_WithNullId() {
    assertFalse(bookService.isValidBookId(null));
  }

  @Test
  void testIsValidBookId_WithZeroId() {
    assertFalse(bookService.isValidBookId(0L));
  }

  @Test
  void testIsValidBookId_WithNegativeId() {
    assertFalse(bookService.isValidBookId(-1L));
  }

  @Test
  void testIsValidIsbn_WithValidIsbn() {
    assertTrue(bookService.isValidIsbn("1234567890"));
    assertTrue(bookService.isValidIsbn("1234567890123"));
  }

  @Test
  void testIsValidIsbn_WithNullIsbn() {
    assertFalse(bookService.isValidIsbn(null));
  }

  @Test
  void testIsValidIsbn_WithEmptyIsbn() {
    assertFalse(bookService.isValidIsbn(""));
  }

  @Test
  void testIsValidIsbn_WithShortIsbn() {
    assertFalse(bookService.isValidIsbn("123"));
  }

  @Test
  void testIsValidTitleSearch_WithValidTitle() {
    assertTrue(bookService.isValidTitleSearch("Test"));
    assertTrue(bookService.isValidTitleSearch("AB"));
  }

  @Test
  void testIsValidTitleSearch_WithNullTitle() {
    assertFalse(bookService.isValidTitleSearch(null));
  }

  @Test
  void testIsValidTitleSearch_WithEmptyTitle() {
    assertFalse(bookService.isValidTitleSearch(""));
  }

  @Test
  void testIsValidTitleSearch_WithShortTitle() {
    assertFalse(bookService.isValidTitleSearch("A"));
  }

  @Test
  void testIsValidAuthorId_WithValidId() {
    assertTrue(bookService.isValidAuthorId(1L));
    assertTrue(bookService.isValidAuthorId(100L));
  }

  @Test
  void testIsValidAuthorId_WithNullId() {
    assertFalse(bookService.isValidAuthorId(null));
  }

  @Test
  void testIsValidAuthorId_WithZeroId() {
    assertFalse(bookService.isValidAuthorId(0L));
  }

  @Test
  void testIsValidAuthorId_WithNegativeId() {
    assertFalse(bookService.isValidAuthorId(-1L));
  }

  @Test
  void testIsValidCopyCounts_WithValidCounts() {
    assertTrue(bookService.isValidCopyCounts(5, 3));
    assertTrue(bookService.isValidCopyCounts(5, 5));
    assertTrue(bookService.isValidCopyCounts(0, 0));
  }

  @Test
  void testIsValidCopyCounts_WithInvalidCounts() {
    assertFalse(bookService.isValidCopyCounts(3, 5)); // available > total
    assertFalse(bookService.isValidCopyCounts(null, 3));
    assertFalse(bookService.isValidCopyCounts(5, null));
    assertFalse(bookService.isValidCopyCounts(-1, 3));
    assertFalse(bookService.isValidCopyCounts(5, -1));
  }
}
