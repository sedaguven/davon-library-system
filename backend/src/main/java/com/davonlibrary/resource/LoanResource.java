package com.davonlibrary.resource;

import com.davonlibrary.dto.LoanDTO;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.service.LoanService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

  @Inject private LoanService loanService;

  @GET
  @Path("/recent")
  public Response getRecentLoans() {
    List<Loan> recentLoans = loanService.getRecentLoans(10);
    return Response.ok(recentLoans).build();
  }

  @GET
  @Path("/loaned-out/count")
  public Response getLoanedOutCount() {
    long count = loanService.countLoanedOut();
    return Response.ok(count).build();
  }

  @GET
  @Path("/overdue/count")
  public Response getOverdueCount() {
    long count = loanService.countOverdue();
    return Response.ok(count).build();
  }

  @GET
  @Path("/user/{userId}")
  public Response getLoansByUserId(@PathParam("userId") Long userId) {
    try {
      List<Loan> loans = loanService.getLoansByUserId(userId);
      List<LoanDTO> dtos = loans.stream()
          .map(l -> new LoanDTO(
              l.id,
              l.bookCopy != null && l.bookCopy.book != null ? l.bookCopy.book.title : "Unknown",
              l.dueDate,
              l.returnDate,
              l.returnDate == null ? (int) ChronoUnit.DAYS.between(LocalDate.now(), l.dueDate) : null
          ))
          .collect(Collectors.toList());
      return Response.ok(dtos).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Failed to retrieve loans: " + e.getMessage())
          .build();
    }
  }

  @PUT
  @Path("/{id}/return")
  @Transactional
  public Response returnLoan(@PathParam("id") Long id) {
    Loan returnedLoan = loanService.returnLoan(id);
    if (returnedLoan != null) {
      LoanDTO dto = new LoanDTO(
          returnedLoan.id,
          returnedLoan.bookCopy != null && returnedLoan.bookCopy.book != null
              ? returnedLoan.bookCopy.book.title
              : "Unknown",
          returnedLoan.dueDate,
          returnedLoan.returnDate,
          returnedLoan.returnDate == null
              ? (int) ChronoUnit.DAYS.between(LocalDate.now(), returnedLoan.dueDate)
              : null);
      return Response.ok(dto).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }
}
