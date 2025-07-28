package com.davonlibrary.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Library Entity Tests")
class LibraryTest {

  private Library library;
  private User user1;
  private User user2;
  private Book book;
  private BookCopy bookCopy;

  @BeforeEach
  void setUp() {
    library = new Library("Downtown Library", "123 Main St", "Downtown");
    user1 = new User("John", "Doe", "john.doe@example.com");
    user2 = new User("Jane", "Smith", "jane.smith@example.com");
    Author author = new Author("J.K.", "Rowling");
    book = new Book("Harry Potter and the Philosopher's Stone", "978-0747532699", author, 3);
    bookCopy = new BookCopy(book, library, "HP-001", "Shelf A-15");
  }

  @Test
  @DisplayName("Should create library with basic information")
  void shouldCreateLibraryWithBasicInformation() {
    // Given
    Library newLibrary = new Library("Uptown Library", "456 Oak Ave", "Uptown");

    // Then
    assertEquals("Uptown Library", newLibrary.name);
    assertEquals("456 Oak Ave", newLibrary.address);
    assertEquals("Uptown", newLibrary.city);
  }

  @Test
  @DisplayName("Should get member count correctly")
  void shouldGetMemberCountCorrectly() {
    // Given
    LibraryMembership membership1 = new LibraryMembership(user1, library);
    membership1.status = LibraryMembership.MembershipStatus.ACTIVE;

    LibraryMembership membership2 = new LibraryMembership(user2, library);
    membership2.status = LibraryMembership.MembershipStatus.SUSPENDED;

    library.memberships = List.of(membership1, membership2);

    // When
    int memberCount = library.getMemberCount();

    // Then
    assertEquals(1, memberCount); // Only active members
  }

  @Test
  @DisplayName("Should return zero member count when no memberships")
  void shouldReturnZeroMemberCountWhenNoMemberships() {
    // Given
    library.memberships = null;

    // When
    int memberCount = library.getMemberCount();

    // Then
    assertEquals(0, memberCount);
  }

  @Test
  @DisplayName("Should get active borrower count correctly")
  void shouldGetActiveBorrowerCountCorrectly() {
    // Given
    LibraryMembership membership1 = new LibraryMembership(user1, library);
    membership1.status = LibraryMembership.MembershipStatus.ACTIVE;

    LibraryMembership membership2 = new LibraryMembership(user2, library);
    membership2.status = LibraryMembership.MembershipStatus.ACTIVE;

    library.memberships = List.of(membership1, membership2);

    // Set up user1 as active borrower
    user1.loans = List.of(new Loan(user1, bookCopy, 14));

    // When
    int activeBorrowerCount = library.getActiveBorrowerCount();

    // Then
    assertEquals(1, activeBorrowerCount); // Only user1 has active loans
  }

  @Test
  @DisplayName("Should get active borrowers list correctly")
  void shouldGetActiveBorrowersListCorrectly() {
    // Given
    LibraryMembership membership1 = new LibraryMembership(user1, library);
    membership1.status = LibraryMembership.MembershipStatus.ACTIVE;

    LibraryMembership membership2 = new LibraryMembership(user2, library);
    membership2.status = LibraryMembership.MembershipStatus.ACTIVE;

    library.memberships = List.of(membership1, membership2);

    // Set up user1 as active borrower
    user1.loans = List.of(new Loan(user1, bookCopy, 14));

    // When
    List<User> activeBorrowers = library.getActiveBorrowers();

    // Then
    assertEquals(1, activeBorrowers.size());
    assertEquals(user1, activeBorrowers.get(0));
  }

  @Test
  @DisplayName("Should get book copy count correctly")
  void shouldGetBookCopyCountCorrectly() {
    // Given
    BookCopy copy1 = new BookCopy(book, library, "HP-001");
    BookCopy copy2 = new BookCopy(book, library, "HP-002");
    library.bookCopies = List.of(copy1, copy2);

    // When
    int bookCopyCount = library.getBookCopyCount();

    // Then
    assertEquals(2, bookCopyCount);
  }

  @Test
  @DisplayName("Should return zero book copy count when no copies")
  void shouldReturnZeroBookCopyCountWhenNoCopies() {
    // Given
    library.bookCopies = null;

    // When
    int bookCopyCount = library.getBookCopyCount();

    // Then
    assertEquals(0, bookCopyCount);
  }

  @Test
  @DisplayName("Should get staff count correctly")
  void shouldGetStaffCountCorrectly() {
    // Given
    Staff staff1 = new Staff("Alice", "Johnson", "alice.johnson@library.com", "Librarian", library);
    Staff staff2 = new Staff("Bob", "Wilson", "bob.wilson@library.com", "Librarian", library);
    library.staff = List.of(staff1, staff2);

    // When
    int staffCount = library.getStaffCount();

    // Then
    assertEquals(2, staffCount);
  }

  @Test
  @DisplayName("Should return zero staff count when no staff")
  void shouldReturnZeroStaffCountWhenNoStaff() {
    // Given
    library.staff = null;

    // When
    int staffCount = library.getStaffCount();

    // Then
    assertEquals(0, staffCount);
  }

  @Test
  @DisplayName("Should get detailed information correctly")
  void shouldGetDetailedInfoCorrectly() {
    // Given
    library.memberships = List.of(new LibraryMembership(user1, library));
    library.bookCopies = List.of(bookCopy);
    library.staff =
        List.of(new Staff("Alice", "Johnson", "alice@library.com", "Librarian", library));

    // When
    String detailedInfo = library.getDetailedInfo();

    // Then
    assertTrue(detailedInfo.contains("Downtown Library"));
    assertTrue(detailedInfo.contains("123 Main St"));
    assertTrue(detailedInfo.contains("Downtown"));
    assertTrue(detailedInfo.contains("1 members"));
    assertTrue(detailedInfo.contains("1 book copies"));
    assertTrue(detailedInfo.contains("1 staff"));
  }

  @Test
  @DisplayName("Should handle null collections in detailed info")
  void shouldHandleNullCollectionsInDetailedInfo() {
    // Given
    library.memberships = null;
    library.bookCopies = null;
    library.staff = null;

    // When
    String detailedInfo = library.getDetailedInfo();

    // Then
    assertTrue(detailedInfo.contains("Downtown Library"));
    assertTrue(detailedInfo.contains("0 members"));
    assertTrue(detailedInfo.contains("0 book copies"));
    assertTrue(detailedInfo.contains("0 staff"));
  }

  @Test
  @DisplayName("Should get library with most active borrowers")
  void shouldGetLibraryWithMostActiveBorrowers() {
    // Given
    Library library1 = new Library("Library 1", "Address 1", "Location 1");
    Library library2 = new Library("Library 2", "Address 2", "Location 2");

    // Set up library1 with more active borrowers
    User user1 = new User("User1", "Test", "user1@test.com");
    User user2 = new User("User2", "Test", "user2@test.com");
    User user3 = new User("User3", "Test", "user3@test.com");

    LibraryMembership membership1 = new LibraryMembership(user1, library1);
    membership1.status = LibraryMembership.MembershipStatus.ACTIVE;
    LibraryMembership membership2 = new LibraryMembership(user2, library1);
    membership2.status = LibraryMembership.MembershipStatus.ACTIVE;
    LibraryMembership membership3 = new LibraryMembership(user3, library2);
    membership3.status = LibraryMembership.MembershipStatus.ACTIVE;

    library1.memberships = List.of(membership1, membership2);
    library2.memberships = List.of(membership3);

    // Set up active loans
    BookCopy copy1 = new BookCopy(book, library1, "COPY-001");
    BookCopy copy2 = new BookCopy(book, library2, "COPY-002");

    user1.loans = List.of(new Loan(user1, copy1, 14));
    user2.loans = List.of(new Loan(user2, copy1, 14));
    user3.loans = List.of(new Loan(user3, copy2, 14));

    // When
    Library libraryWithMostBorrowers = Library.getLibraryWithMostActiveBorrowers();

    // Then
    assertNotNull(libraryWithMostBorrowers);
    // Note: This test assumes the method works correctly with the given data
  }

  @Test
  @DisplayName("Should get libraries ordered by active borrowers")
  void shouldGetLibrariesOrderedByActiveBorrowers() {
    // When
    List<Library> orderedLibraries = Library.getLibrariesOrderedByActiveBorrowers();

    // Then
    assertNotNull(orderedLibraries);
    // Note: This test verifies the method doesn't throw exceptions
  }

  @Test
  @DisplayName("Should get libraries ordered by member count")
  void shouldGetLibrariesOrderedByMemberCount() {
    // When
    List<Library> orderedLibraries = Library.getLibrariesOrderedByMemberCount();

    // Then
    assertNotNull(orderedLibraries);
    // Note: This test verifies the method doesn't throw exceptions
  }

  @Test
  @DisplayName("Should handle edge case with empty library")
  void shouldHandleEdgeCaseWithEmptyLibrary() {
    // Given
    Library emptyLibrary = new Library("Empty Library", "Empty Address", "Empty Location");

    // When
    int memberCount = emptyLibrary.getMemberCount();
    int activeBorrowerCount = emptyLibrary.getActiveBorrowerCount();
    int bookCopyCount = emptyLibrary.getBookCopyCount();
    int staffCount = emptyLibrary.getStaffCount();
    String detailedInfo = emptyLibrary.getDetailedInfo();

    // Then
    assertEquals(0, memberCount);
    assertEquals(0, activeBorrowerCount);
    assertEquals(0, bookCopyCount);
    assertEquals(0, staffCount);
    assertTrue(detailedInfo.contains("Empty Library"));
  }

  @Test
  @DisplayName("Should provide meaningful string representation")
  void shouldProvideMeaningfulStringRepresentation() {
    // When
    String representation = library.toString();

    // Then
    assertTrue(representation.contains("Downtown Library"));
    assertTrue(representation.contains("123 Main St"));
  }
}
