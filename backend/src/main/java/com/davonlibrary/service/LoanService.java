package com.davonlibrary.service;

import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.Fine;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.repository.BookCopyRepository;
import com.davonlibrary.repository.FineRepository;
import com.davonlibrary.repository.LoanRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class LoanService {
  @Inject LoanRepository loanRepository;
  @Inject BookCopyRepository bookCopyRepository;
  @Inject FineRepository fineRepository;

  public List<Loan> getRecentLoans(int limit) {
    return loanRepository.findRecent(limit);
  }

  public List<Loan> getLoansByUserId(Long userId) {
    return loanRepository.findByUser(userId);
  }

  public long countLoanedOut() {
    return loanRepository.count("returnDate is null");
  }

  public long countOverdue() {
    return loanRepository.count("returnDate is null and dueDate < ?1", LocalDate.now());
  }

  @Transactional
  public Loan createLoan(Loan loan) {
    loanRepository.persist(loan);
    return loan;
  }

  @Transactional
  public Loan returnLoan(Long loanId) {
    Loan loan = loanRepository.findById(loanId);
    if (loan != null && loan.returnDate == null) {
      loan.returnDate = LocalDateTime.now();
      BookCopy bookCopy = loan.bookCopy;
      bookCopy.status = BookCopy.BookCopyStatus.AVAILABLE;
      bookCopyRepository.persist(bookCopy);

      if (loan.returnDate.isAfter(loan.dueDate.atStartOfDay())) {
        long daysOverdue = ChronoUnit.DAYS.between(loan.dueDate, loan.returnDate.toLocalDate());
        BigDecimal fineAmount = new BigDecimal("0.50").multiply(new BigDecimal(daysOverdue));
        Fine fine = new Fine(loan, fineAmount, "Overdue return");
        fineRepository.persist(fine);
      }
      loanRepository.persist(loan);
    }
    return loan;
  }

  @Transactional
  public Loan extendLoan(Long loanId) {
    Loan loan = loanRepository.findById(loanId);
    if (loan != null
        && loan.returnDate == null
        && loan.extensionsCount < loan.maxExtensionsAllowed) {
      loan.dueDate = loan.dueDate.plusWeeks(2);
      loan.extensionsCount++;
      loanRepository.persist(loan);
    }
    return loan;
  }

  public boolean isValidLoan(Loan loan) {
    return loan != null
        && loan.user != null
        && loan.bookCopy != null
        && loan.loanDate != null
        && loan.dueDate != null;
  }

  public boolean isValidLoanId(Long loanId) {
    return loanId != null && loanId > 0;
  }

  public boolean isValidUserId(Long userId) {
    return userId != null && userId > 0;
  }

  public boolean isValidBookCopyId(Long bookCopyId) {
    return bookCopyId != null && bookCopyId > 0;
  }
}
