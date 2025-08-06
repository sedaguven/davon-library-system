package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class LoanServiceTest {

  @Inject LoanService loanService;

  @Test
  void testServiceInjection() {
    assertNotNull(loanService);
  }

  @Test
  void testIsValidLoan_WithValidLoan() {
    Loan loan = new Loan();
    loan.user = new User();
    loan.bookCopy = new BookCopy();

    assertTrue(loanService.isValidLoan(loan));
  }

  @Test
  void testIsValidLoan_WithNullLoan() {
    assertFalse(loanService.isValidLoan(null));
  }

  @Test
  void testIsValidLoan_WithNullUser() {
    Loan loan = new Loan();
    loan.user = null;
    loan.bookCopy = new BookCopy();

    assertFalse(loanService.isValidLoan(loan));
  }

  @Test
  void testIsValidLoan_WithNullBookCopy() {
    Loan loan = new Loan();
    loan.user = new User();
    loan.bookCopy = null;

    assertFalse(loanService.isValidLoan(loan));
  }

  @Test
  void testIsValidLoanId_WithValidId() {
    assertTrue(loanService.isValidLoanId(1L));
    assertTrue(loanService.isValidLoanId(100L));
  }

  @Test
  void testIsValidLoanId_WithNullId() {
    assertFalse(loanService.isValidLoanId(null));
  }

  @Test
  void testIsValidLoanId_WithZeroId() {
    assertFalse(loanService.isValidLoanId(0L));
  }

  @Test
  void testIsValidLoanId_WithNegativeId() {
    assertFalse(loanService.isValidLoanId(-1L));
  }

  @Test
  void testIsValidUserId_WithValidId() {
    assertTrue(loanService.isValidUserId(1L));
    assertTrue(loanService.isValidUserId(100L));
  }

  @Test
  void testIsValidUserId_WithNullId() {
    assertFalse(loanService.isValidUserId(null));
  }

  @Test
  void testIsValidUserId_WithZeroId() {
    assertFalse(loanService.isValidUserId(0L));
  }

  @Test
  void testIsValidUserId_WithNegativeId() {
    assertFalse(loanService.isValidUserId(-1L));
  }

  @Test
  void testIsValidBookCopyId_WithValidId() {
    assertTrue(loanService.isValidBookCopyId(1L));
    assertTrue(loanService.isValidBookCopyId(100L));
  }

  @Test
  void testIsValidBookCopyId_WithNullId() {
    assertFalse(loanService.isValidBookCopyId(null));
  }

  @Test
  void testIsValidBookCopyId_WithZeroId() {
    assertFalse(loanService.isValidBookCopyId(0L));
  }

  @Test
  void testIsValidBookCopyId_WithNegativeId() {
    assertFalse(loanService.isValidBookCopyId(-1L));
  }
}
