package com.davonlibrary.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Book Entity Tests")
class BookTest {

  private Book book;
  private Author author;
  private Library library;

  @BeforeEach
  void setUp() {
    author = new Author("J.K.", "Rowling");
    book = new Book("Harry Potter and the Philosopher's Stone", "978-0747532699", author, 3);
    library = new Library("Downtown Library", "123 Main St", "Downtown");
  }

  @Test
  @DisplayName("Should create book with basic information")
  void shouldCreateBookWithBasicInformation() {
    // Given
    Book newBook = new Book("Test Book", "978-1234567890", author, 2);

    // Then
    assertEquals("Test Book", newBook.title);
    assertEquals("978-1234567890", newBook.isbn);
    assertEquals(author, newBook.author);
    assertEquals(2, newBook.totalCopies);
    assertEquals(2, newBook.availableCopies);
  }

  @Test
  @DisplayName("Should create book with minimal information")
  void shouldCreateBookWithMinimalInformation() {
    // Given
    Book newBook = new Book("Test Book", author);

    // Then
    assertEquals("Test Book", newBook.title);
    assertEquals(author, newBook.author);
    assertEquals(1, newBook.totalCopies);
    assertEquals(1, newBook.availableCopies);
  }

  @Test
  @DisplayName("Should check availability correctly")
  void shouldCheckAvailabilityCorrectly() {
    // Given
    Book availableBook = new Book("Available Book", "978-1234567890", author, 2);
    Book unavailableBook = new Book("Unavailable Book", "978-1234567891", author, 0);
    unavailableBook.availableCopies = 0;

    // When & Then
    assertTrue(availableBook.isAvailable());
    assertFalse(unavailableBook.isAvailable());
  }

  @Test
  @DisplayName("Should borrow copy successfully when available")
  void shouldBorrowCopySuccessfullyWhenAvailable() {
    // Given
    int initialAvailable = book.availableCopies;

    // When
    boolean success = book.borrowCopy();

    // Then
    assertTrue(success);
    assertEquals(initialAvailable - 1, book.availableCopies);
  }

  @Test
  @DisplayName("Should not borrow copy when not available")
  void shouldNotBorrowCopyWhenNotAvailable() {
    // Given
    book.availableCopies = 0;

    // When
    boolean success = book.borrowCopy();

    // Then
    assertFalse(success);
    assertEquals(0, book.availableCopies);
  }

  @Test
  @DisplayName("Should return copy successfully")
  void shouldReturnCopySuccessfully() {
    // Given
    book.availableCopies = 1;
    int initialAvailable = book.availableCopies;

    // When
    book.returnCopy();

    // Then
    assertEquals(initialAvailable + 1, book.availableCopies);
  }

  @Test
  @DisplayName("Should not exceed total copies when returning")
  void shouldNotExceedTotalCopiesWhenReturning() {
    // Given
    book.availableCopies = book.totalCopies;

    // When
    book.returnCopy();

    // Then
    assertEquals(book.totalCopies, book.availableCopies);
  }

  @Test
  @DisplayName("Should get detailed information correctly")
  void shouldGetDetailedInfoCorrectly() {
    // When
    String detailedInfo = book.getDetailedInfo();

    // Then
    assertTrue(detailedInfo.contains("Harry Potter and the Philosopher's Stone"));
    assertTrue(detailedInfo.contains("J.K. Rowling"));
    assertTrue(detailedInfo.contains("978-0747532699"));
    assertTrue(detailedInfo.contains("3 copies available"));
  }

  @Test
  @DisplayName("Should get detailed info with null author")
  void shouldGetDetailedInfoWithNullAuthor() {
    // Given
    book.author = null;

    // When
    String detailedInfo = book.getDetailedInfo();

    // Then
    assertTrue(detailedInfo.contains("Unknown Author"));
  }

  @Test
  @DisplayName("Should get detailed info with null ISBN")
  void shouldGetDetailedInfoWithNullIsbn() {
    // Given
    book.isbn = null;

    // When
    String detailedInfo = book.getDetailedInfo();

    // Then
    assertTrue(detailedInfo.contains("No ISBN"));
  }

  @Test
  @DisplayName("Should get all copies correctly")
  void shouldGetAllCopiesCorrectly() {
    // Given
    BookCopy copy1 = new BookCopy(book, library, "COPY-001");
    BookCopy copy2 = new BookCopy(book, library, "COPY-002");
    book.bookCopies = List.of(copy1, copy2);

    // When
    List<BookCopy> allCopies = book.getAllCopies();

    // Then
    assertEquals(2, allCopies.size());
    assertTrue(allCopies.contains(copy1));
    assertTrue(allCopies.contains(copy2));
  }

  @Test
  @DisplayName("Should handle null book copies gracefully")
  void shouldHandleNullBookCopiesGracefully() {
    // Given
    book.bookCopies = null;

    // When
    List<BookCopy> allCopies = book.getAllCopies();

    // Then
    assertNotNull(allCopies);
    assertTrue(allCopies.isEmpty());
  }

  @Test
  @DisplayName("Should get current reservations correctly")
  void shouldGetCurrentReservationsCorrectly() {
    // When
    List<Reservation> currentReservations = book.getCurrentReservations();

    // Then
    assertNotNull(currentReservations);
    assertTrue(currentReservations.isEmpty());
  }

  @Test
  @DisplayName("Should handle multiple borrow and return operations")
  void shouldHandleMultipleBorrowAndReturnOperations() {
    // Given
    int initialAvailable = book.availableCopies;

    // When - Borrow twice
    boolean borrow1 = book.borrowCopy();
    boolean borrow2 = book.borrowCopy();

    // Then
    assertTrue(borrow1);
    assertTrue(borrow2);
    assertEquals(initialAvailable - 2, book.availableCopies);

    // When - Return once
    book.returnCopy();

    // Then
    assertEquals(initialAvailable - 1, book.availableCopies);
  }

  @Test
  @DisplayName("Should handle edge case with null available copies")
  void shouldHandleNullAvailableCopies() {
    // Given
    book.availableCopies = null;

    // When
    boolean isAvailable = book.isAvailable();
    boolean borrowSuccess = book.borrowCopy();

    // Then
    assertFalse(isAvailable);
    assertFalse(borrowSuccess);
  }

  @Test
  @DisplayName("Should handle edge case with null total copies")
  void shouldHandleNullTotalCopies() {
    // Given
    book.totalCopies = null;
    book.availableCopies = 1;

    // When
    book.returnCopy();

    // Then
    assertEquals(1, book.availableCopies); // Should not increment
  }
}
