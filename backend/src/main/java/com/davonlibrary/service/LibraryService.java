package com.davonlibrary.service;

import com.davonlibrary.entity.Book;
import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.Reservation;
import com.davonlibrary.entity.User;
import com.davonlibrary.repository.BookRepository;
import com.davonlibrary.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
public class LibraryService {

  @PersistenceContext EntityManager entityManager;

  @Inject private BookRepository bookRepository;
  @Inject private LoanService loanService;
  @Inject private ReservationService reservationService;
  @Inject private UserRepository userRepository;

  @Transactional
  public Loan borrowBook(Long userId, Long bookId) {
    // Set QUOTED_IDENTIFIER to ON for this connection
    entityManager.createNativeQuery("SET QUOTED_IDENTIFIER ON").executeUpdate();

    try {
      // Use the new method to fetch the book and its copies together
      Book book = bookRepository.findByIdWithCopies(bookId);
      if (book == null) {
        throw new IllegalArgumentException("Book not found with ID: " + bookId);
      }

      User user = userRepository.findById(userId);
      if (user == null) {
        throw new IllegalArgumentException("User not found with ID: " + userId);
      }

      // Find the first available copy
      BookCopy availableCopy =
          book.bookCopies.stream()
              .filter(copy -> BookCopy.BookCopyStatus.AVAILABLE.equals(copy.status))
              .findFirst()
              .orElseThrow(() -> new IllegalStateException("No available copies of the book"));

      // Create a new loan
      Loan loan = new Loan();
      loan.user = user;
      loan.bookCopy = availableCopy;
      loan.loanDate = LocalDateTime.now();
      loan.dueDate = LocalDate.now().plusDays(14); // 2 weeks loan period
      loan.status = Loan.LoanStatus.ACTIVE;
      loan.maxExtensionsAllowed = 3;
      loan.extensionsCount = 0;

      // Save the loan
      loanService.createLoan(loan);

      // Update book copy status
      availableCopy.status = BookCopy.BookCopyStatus.CHECKED_OUT;
      availableCopy.persist();

      return loan;
    } catch (Exception e) {
      System.err.println("### A critical error occurred in borrowBook ###");
      e.printStackTrace();
      throw e;
    }
  }

  @Transactional
  public Reservation reserveBook(Long userId, Long bookId) {
    // Set QUOTED_IDENTIFIER to ON for this connection
    entityManager.createNativeQuery("SET QUOTED_IDENTIFIER ON").executeUpdate();

    try {
      // Use the new method to fetch the book and its copies together
      Book book = bookRepository.findByIdWithCopies(bookId);
      if (book == null) {
        throw new IllegalArgumentException("Book not found with ID: " + bookId);
      }

      User user = userRepository.findById(userId);
      if (user == null) {
        throw new IllegalArgumentException("User not found with ID: " + userId);
      }

      long queuePosition = reservationService.countActiveReservationsByBook(book.id) + 1;

      Reservation reservation = new Reservation();
      reservation.user = user;
      reservation.book = book;
      reservation.reservationDate = LocalDateTime.now();
      reservation.status = Reservation.ReservationStatus.ACTIVE;
      reservation.expiryDate = LocalDate.now().plusDays(7); // 7 days to pick up
      reservation.queuePosition = (int) queuePosition;

      reservationService.createReservation(reservation);

      return reservation;
    } catch (Exception e) {
      System.err.println("### A critical error occurred in reserveBook ###");
      e.printStackTrace();
      throw e;
    }
  }
}
