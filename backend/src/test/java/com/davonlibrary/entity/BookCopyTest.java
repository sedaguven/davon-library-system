package com.davonlibrary.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BookCopy Entity Tests")
class BookCopyTest {

  private BookCopy bookCopy;
  private Book book;
  private Library library;
  private User user;

  @BeforeEach
  void setUp() {
    Author author = new Author("J.K.", "Rowling");
    book = new Book("Harry Potter and the Philosopher's Stone", "978-0747532699", author, 3);
    library = new Library("Downtown Library", "123 Main St", "Downtown");
    bookCopy = new BookCopy(book, library, "HP-001", "Shelf A-15");
    user = new User("John", "Doe", "john.doe@example.com");
  }

  @Test
  @DisplayName("Should create book copy with basic information")
  void shouldCreateBookCopyWithBasicInformation() {
    // Given
    BookCopy newCopy = new BookCopy(book, library, "HP-002");

    // Then
    assertEquals("HP-002", newCopy.barcode);
    assertEquals(book, newCopy.book);
    assertEquals(library, newCopy.library);
    assertTrue(newCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.AVAILABLE, newCopy.status);
  }

  @Test
  @DisplayName("Should create book copy with location")
  void shouldCreateBookCopyWithLocation() {
    // Given
    BookCopy newCopy = new BookCopy(book, library, "HP-003", "Shelf B-20");

    // Then
    assertEquals("HP-003", newCopy.barcode);
    assertEquals("Shelf B-20", newCopy.location);
    assertTrue(newCopy.isAvailable);
  }

  @Test
  @DisplayName("Should check out copy successfully")
  void shouldCheckOutCopySuccessfully() {
    // Given
    assertTrue(bookCopy.isAvailable);
    int initialAvailable = book.availableCopies;

    // When
    bookCopy.checkOut();

    // Then
    assertFalse(bookCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.CHECKED_OUT, bookCopy.status);
    assertEquals(initialAvailable - 1, book.availableCopies);
  }

  @Test
  @DisplayName("Should not check out already checked out copy")
  void shouldNotCheckOutAlreadyCheckedOutCopy() {
    // Given
    bookCopy.checkOut();
    int initialAvailable = book.availableCopies;

    // When
    bookCopy.checkOut();

    // Then
    assertFalse(bookCopy.isAvailable);
    assertEquals(initialAvailable, book.availableCopies); // Should not change
  }

  @Test
  @DisplayName("Should return copy successfully")
  void shouldReturnCopySuccessfully() {
    // Given
    bookCopy.checkOut();
    int initialAvailable = book.availableCopies;

    // When
    bookCopy.returnCopy();

    // Then
    assertTrue(bookCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.AVAILABLE, bookCopy.status);
    assertEquals(initialAvailable + 1, book.availableCopies);
  }

  @Test
  @DisplayName("Should not return already available copy")
  void shouldNotReturnAlreadyAvailableCopy() {
    // Given
    int initialAvailable = book.availableCopies;

    // When
    bookCopy.returnCopy();

    // Then
    assertTrue(bookCopy.isAvailable);
    assertEquals(initialAvailable, book.availableCopies); // Should not change
  }

  @Test
  @DisplayName("Should mark copy as damaged")
  void shouldMarkCopyAsDamaged() {
    // Given
    assertTrue(bookCopy.isAvailable);

    // When
    bookCopy.markAsDamaged();

    // Then
    assertFalse(bookCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.DAMAGED, bookCopy.status);
  }

  @Test
  @DisplayName("Should send copy to maintenance")
  void shouldSendCopyToMaintenance() {
    // Given
    String reason = "Damaged spine";

    // When
    bookCopy.sendToMaintenance(reason);

    // Then
    assertFalse(bookCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.MAINTENANCE, bookCopy.status);
    assertEquals(reason, bookCopy.notes);
  }

  @Test
  @DisplayName("Should get current loan correctly")
  void shouldGetCurrentLoanCorrectly() {
    // Given
    Loan activeLoan = new Loan(user, bookCopy, LocalDate.now().plusDays(14));
    activeLoan.returnDate = null; // Active loan

    Loan returnedLoan = new Loan(user, bookCopy, LocalDate.now().plusDays(14));
    returnedLoan.returnDate = LocalDateTime.now(); // Returned loan

    bookCopy.loans = List.of(activeLoan, returnedLoan);

    // When
    Loan currentLoan = bookCopy.getCurrentLoan();

    // Then
    assertEquals(activeLoan, currentLoan);
  }

  @Test
  @DisplayName("Should return null when no current loan")
  void shouldReturnNullWhenNoCurrentLoan() {
    // Given
    Loan returnedLoan = new Loan(user, bookCopy, LocalDate.now().plusDays(14));
    returnedLoan.returnDate = LocalDateTime.now(); // Returned loan

    bookCopy.loans = List.of(returnedLoan);

    // When
    Loan currentLoan = bookCopy.getCurrentLoan();

    // Then
    assertNull(currentLoan);
  }

  @Test
  @DisplayName("Should check if copy is on loan")
  void shouldCheckIfCopyIsOnLoan() {
    // Given
    Loan activeLoan = new Loan(user, bookCopy, LocalDate.now().plusDays(14));
    activeLoan.returnDate = null;
    bookCopy.loans = List.of(activeLoan);

    // When
    boolean isOnLoan = bookCopy.isOnLoan();

    // Then
    assertTrue(isOnLoan);
  }

  @Test
  @DisplayName("Should check if copy is not on loan")
  void shouldCheckIfCopyIsNotOnLoan() {
    // Given
    Loan returnedLoan = new Loan(user, bookCopy, LocalDate.now().plusDays(14));
    returnedLoan.returnDate = LocalDateTime.now();
    bookCopy.loans = List.of(returnedLoan);

    // When
    boolean isOnLoan = bookCopy.isOnLoan();

    // Then
    assertFalse(isOnLoan);
  }

  @Test
  @DisplayName("Should handle null loans gracefully")
  void shouldHandleNullLoansGracefully() {
    // Given
    bookCopy.loans = null;

    // When
    Loan currentLoan = bookCopy.getCurrentLoan();
    boolean isOnLoan = bookCopy.isOnLoan();

    // Then
    assertNull(currentLoan);
    assertFalse(isOnLoan);
  }

  @Test
  @DisplayName("Should handle null book gracefully during checkout")
  void shouldHandleNullBookGracefullyDuringCheckout() {
    // Given
    bookCopy.book = null;

    // When
    bookCopy.checkOut();

    // Then
    assertFalse(bookCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.CHECKED_OUT, bookCopy.status);
  }

  @Test
  @DisplayName("Should handle null book gracefully during return")
  void shouldHandleNullBookGracefullyDuringReturn() {
    // Given
    bookCopy.book = null;
    bookCopy.checkOut();

    // When
    bookCopy.returnCopy();

    // Then
    assertTrue(bookCopy.isAvailable);
    assertEquals(BookCopy.BookCopyStatus.AVAILABLE, bookCopy.status);
  }

  @Test
  @DisplayName("Should maintain status consistency during operations")
  void shouldMaintainStatusConsistencyDuringOperations() {
    // Given
    bookCopy.status = BookCopy.BookCopyStatus.AVAILABLE;

    // When - Check out
    bookCopy.checkOut();

    // Then
    assertEquals(BookCopy.BookCopyStatus.CHECKED_OUT, bookCopy.status);
    assertFalse(bookCopy.isAvailable);

    // When - Return
    bookCopy.returnCopy();

    // Then
    assertEquals(BookCopy.BookCopyStatus.AVAILABLE, bookCopy.status);
    assertTrue(bookCopy.isAvailable);
  }

  @Test
  @DisplayName("Should handle maintenance status correctly")
  void shouldHandleMaintenanceStatusCorrectly() {
    // Given
    bookCopy.status = BookCopy.BookCopyStatus.AVAILABLE;

    // When
    bookCopy.sendToMaintenance("Spine damage");

    // Then
    assertEquals(BookCopy.BookCopyStatus.MAINTENANCE, bookCopy.status);
    assertFalse(bookCopy.isAvailable);
    assertEquals("Spine damage", bookCopy.notes);
  }

  @Test
  @DisplayName("Should handle damaged status correctly")
  void shouldHandleDamagedStatusCorrectly() {
    // Given
    bookCopy.status = BookCopy.BookCopyStatus.AVAILABLE;

    // When
    bookCopy.markAsDamaged();

    // Then
    assertEquals(BookCopy.BookCopyStatus.DAMAGED, bookCopy.status);
    assertFalse(bookCopy.isAvailable);
  }

  @Test
  @DisplayName("Should provide meaningful string representation")
  void shouldProvideMeaningfulStringRepresentation() {
    // When
    String representation = bookCopy.toString();

    // Then
    assertTrue(representation.contains("HP-001"));
    assertTrue(representation.contains("Harry Potter and the Philosopher's Stone"));
    assertTrue(representation.contains("true")); // isAvailable
  }
}
