package com.davonlibrary.resource;

import com.davonlibrary.entity.Fine;
import com.davonlibrary.service.FineService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** REST resource for fine management operations. */
@Path("/api/fines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FineResource {

  @Inject FineService fineService;

  /**
   * Gets all active fines.
   *
   * @return list of active fines
   */
  @GET
  @Path("/active")
  public Response getActiveFines() {
    List<Fine> fines = fineService.getActiveFinesForUser(null);
    return Response.ok(fines).build();
  }

  /**
   * Gets active fines for a specific user.
   *
   * @param userId the user ID
   * @return list of active fines for the user
   */
  @GET
  @Path("/user/{userId}/active")
  public Response getActiveFinesForUser(@PathParam("userId") Long userId) {
    List<Fine> fines = fineService.getActiveFinesForUser(userId);
    return Response.ok(fines).build();
  }

  /**
   * Gets a specific fine by ID.
   *
   * @param fineId the fine ID
   * @return the fine if found
   */
  @GET
  @Path("/{fineId}")
  public Response getFine(@PathParam("fineId") Long fineId) {
    Fine fine = Fine.findById(fineId);
    if (fine == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(fine).build();
  }

  /**
   * Processes payment for a fine.
   *
   * @param fineId the fine ID
   * @param paymentRequest the payment request
   * @return payment result
   */
  @POST
  @Path("/{fineId}/pay")
  public Response payFine(@PathParam("fineId") Long fineId, PaymentRequest paymentRequest) {
    boolean success =
        fineService.processPayment(
            fineId,
            paymentRequest.amount,
            paymentRequest.paymentMethod,
            paymentRequest.transactionId);

    if (success) {
      return Response.ok().entity("Payment processed successfully").build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Payment processing failed")
          .build();
    }
  }

  /**
   * Waives a fine.
   *
   * @param fineId the fine ID
   * @param waiverRequest the waiver request
   * @return waiver result
   */
  @POST
  @Path("/{fineId}/waive")
  public Response waiveFine(@PathParam("fineId") Long fineId, WaiverRequest waiverRequest) {
    boolean success = fineService.waiveFine(fineId, waiverRequest.reason, waiverRequest.waivedBy);

    if (success) {
      return Response.ok().entity("Fine waived successfully").build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST).entity("Fine waiver failed").build();
    }
  }

  /**
   * Applies a discount to a fine.
   *
   * @param fineId the fine ID
   * @param discountRequest the discount request
   * @return discount result
   */
  @POST
  @Path("/{fineId}/discount")
  public Response applyDiscount(@PathParam("fineId") Long fineId, DiscountRequest discountRequest) {
    boolean success = fineService.applyDiscount(fineId, discountRequest.percentage);

    if (success) {
      return Response.ok().entity("Discount applied successfully").build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Discount application failed")
          .build();
    }
  }

  /**
   * Gets fine statistics for a user.
   *
   * @param userId the user ID
   * @return fine statistics
   */
  @GET
  @Path("/user/{userId}/statistics")
  public Response getFineStatistics(@PathParam("userId") Long userId) {
    Object[] statistics = fineService.getFineStatisticsForUser(userId);

    FineStatisticsResponse response = new FineStatisticsResponse();
    response.totalFines = (Long) statistics[0];
    response.paidFines = (Long) statistics[1];
    response.waivedFines = (Long) statistics[2];
    response.outstandingAmount = (BigDecimal) statistics[3];

    return Response.ok(response).build();
  }

  /**
   * Gets total outstanding amount for a user.
   *
   * @param userId the user ID
   * @return total outstanding amount
   */
  @GET
  @Path("/user/{userId}/outstanding")
  public Response getOutstandingAmount(@PathParam("userId") Long userId) {
    BigDecimal amount = fineService.getTotalOutstandingAmountForUser(userId);
    return Response.ok().entity(new OutstandingAmountResponse(amount)).build();
  }

  /**
   * Checks if user has outstanding fines.
   *
   * @param userId the user ID
   * @return whether user has outstanding fines
   */
  @GET
  @Path("/user/{userId}/has-outstanding")
  public Response hasOutstandingFines(@PathParam("userId") Long userId) {
    boolean hasOutstanding = fineService.hasOutstandingFines(userId);
    return Response.ok().entity(new HasOutstandingResponse(hasOutstanding)).build();
  }

  /**
   * Gets overdue fines.
   *
   * @return list of overdue fines
   */
  @GET
  @Path("/overdue")
  public Response getOverdueFines() {
    List<Fine> fines = fineService.getOverdueFines();
    return Response.ok(fines).build();
  }

  /**
   * Recalculates all fines.
   *
   * @return recalculation result
   */
  @POST
  @Path("/recalculate")
  public Response recalculateFines() {
    int recalculatedCount = fineService.recalculateAllFines();
    return Response.ok().entity(new RecalculationResponse(recalculatedCount)).build();
  }

  /**
   * Creates fines for overdue loans.
   *
   * @return creation result
   */
  @POST
  @Path("/create-for-overdue")
  public Response createFinesForOverdueLoans() {
    int createdCount = fineService.createFinesForOverdueLoans();
    return Response.ok().entity(new FineCreationResponse(createdCount)).build();
  }

  /**
   * Gets fine collection report for a date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return collection report
   */
  @GET
  @Path("/collection-report")
  public Response getCollectionReport(
      @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {

    LocalDateTime start = LocalDateTime.parse(startDate);
    LocalDateTime end = LocalDateTime.parse(endDate);

    BigDecimal collected = fineService.getTotalCollectionBetween(start, end);
    BigDecimal waived = fineService.getTotalWaivedBetween(start, end);

    CollectionReportResponse report = new CollectionReportResponse();
    report.startDate = start;
    report.endDate = end;
    report.totalCollected = collected;
    report.totalWaived = waived;
    report.netCollection = collected.subtract(waived);

    return Response.ok(report).build();
  }

  /**
   * Gets progressive fine amount for a loan.
   *
   * @param loanId the loan ID
   * @return progressive fine amount
   */
  @GET
  @Path("/loan/{loanId}/progressive")
  public Response getProgressiveFineAmount(@PathParam("loanId") Long loanId) {
    BigDecimal amount = fineService.getProgressiveFineAmount(loanId);
    return Response.ok().entity(new ProgressiveFineResponse(amount)).build();
  }

  // Request/Response classes

  public static class PaymentRequest {
    public BigDecimal amount;
    public String paymentMethod;
    public String transactionId;
  }

  public static class WaiverRequest {
    public String reason;
    public String waivedBy;
  }

  public static class DiscountRequest {
    public int percentage;
  }

  public static class FineStatisticsResponse {
    public Long totalFines;
    public Long paidFines;
    public Long waivedFines;
    public BigDecimal outstandingAmount;
  }

  public static class OutstandingAmountResponse {
    public BigDecimal amount;

    public OutstandingAmountResponse(BigDecimal amount) {
      this.amount = amount;
    }
  }

  public static class HasOutstandingResponse {
    public boolean hasOutstanding;

    public HasOutstandingResponse(boolean hasOutstanding) {
      this.hasOutstanding = hasOutstanding;
    }
  }

  public static class RecalculationResponse {
    public int recalculatedCount;

    public RecalculationResponse(int recalculatedCount) {
      this.recalculatedCount = recalculatedCount;
    }
  }

  public static class FineCreationResponse {
    public int createdCount;

    public FineCreationResponse(int createdCount) {
      this.createdCount = createdCount;
    }
  }

  public static class CollectionReportResponse {
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public BigDecimal totalCollected;
    public BigDecimal totalWaived;
    public BigDecimal netCollection;
  }

  public static class ProgressiveFineResponse {
    public BigDecimal amount;

    public ProgressiveFineResponse(BigDecimal amount) {
      this.amount = amount;
    }
  }
}
