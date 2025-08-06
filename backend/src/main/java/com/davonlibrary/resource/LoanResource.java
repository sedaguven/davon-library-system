package com.davonlibrary.resource;

import com.davonlibrary.entity.Loan;
import com.davonlibrary.service.LoanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

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
      return Response.ok(loans).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Failed to retrieve loans: " + e.getMessage())
          .build();
    }
  }

  @PUT
  @Path("/{id}/return")
  public Response returnLoan(@PathParam("id") Long id) {
    Loan returnedLoan = loanService.returnLoan(id);
    if (returnedLoan != null) {
      return Response.ok(returnedLoan).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }
}
