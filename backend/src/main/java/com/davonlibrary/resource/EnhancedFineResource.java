package com.davonlibrary.resource;

import com.davonlibrary.entity.Fine;
import com.davonlibrary.entity.Loan;
import com.davonlibrary.entity.User;
import com.davonlibrary.service.EnhancedFineService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Map;

/** Enhanced REST resource for fine management with advanced features. */
@Path("/api/enhanced-fines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnhancedFineResource {

  @Inject EnhancedFineService enhancedFineService;

  /**
   * Calculates enhanced fine with weekend and holiday multipliers.
   *
   * @param loanId the loan ID
   * @return the calculated fine amount
   */
  @GET
  @Path("/calculate-enhanced/{loanId}")
  public Response calculateEnhancedFine(@PathParam("loanId") Long loanId) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    BigDecimal fineAmount = enhancedFineService.calculateEnhancedFine(loan);
    return Response.ok().entity(new FineCalculationResponse(fineAmount)).build();
  }

  /**
   * Calculates fine with grace period.
   *
   * @param loanId the loan ID
   * @return the calculated fine amount
   */
  @GET
  @Path("/calculate-grace-period/{loanId}")
  public Response calculateFineWithGracePeriod(@PathParam("loanId") Long loanId) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    BigDecimal fineAmount = enhancedFineService.calculateFineWithGracePeriod(loan);
    return Response.ok().entity(new FineCalculationResponse(fineAmount)).build();
  }

  /**
   * Calculates fine with loyalty discount.
   *
   * @param loanId the loan ID
   * @return the calculated fine amount
   */
  @GET
  @Path("/calculate-loyalty-discount/{loanId}")
  public Response calculateFineWithLoyaltyDiscount(@PathParam("loanId") Long loanId) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    BigDecimal fineAmount = enhancedFineService.calculateFineWithLoyaltyDiscount(loan, loan.user);
    return Response.ok().entity(new FineCalculationResponse(fineAmount)).build();
  }

  /**
   * Calculates volume discount for a user.
   *
   * @param userId the user ID
   * @return the volume discount percentage
   */
  @GET
  @Path("/volume-discount/{userId}")
  public Response calculateVolumeDiscount(@PathParam("userId") Long userId) {
    User user = User.findById(userId);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
    }

    BigDecimal discountPercentage = enhancedFineService.calculateVolumeDiscount(user);
    return Response.ok().entity(new DiscountResponse(discountPercentage)).build();
  }

  /**
   * Calculates fine with seasonal adjustments.
   *
   * @param loanId the loan ID
   * @return the calculated fine amount
   */
  @GET
  @Path("/calculate-seasonal/{loanId}")
  public Response calculateFineWithSeasonalAdjustments(@PathParam("loanId") Long loanId) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    BigDecimal fineAmount = enhancedFineService.calculateFineWithSeasonalAdjustments(loan);
    return Response.ok().entity(new FineCalculationResponse(fineAmount)).build();
  }

  /**
   * Calculates installment amount for a fine.
   *
   * @param fineId the fine ID
   * @param request the installment request
   * @return the installment amount
   */
  @POST
  @Path("/{fineId}/installment")
  public Response calculateInstallmentAmount(
      @PathParam("fineId") Long fineId, InstallmentRequest request) {
    Fine fine = Fine.findById(fineId);
    if (fine == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Fine not found").build();
    }

    BigDecimal installmentAmount =
        enhancedFineService.calculateInstallmentAmount(fine.amount, request.installmentCount);
    return Response.ok().entity(new InstallmentResponse(installmentAmount)).build();
  }

  /**
   * Gets fine statistics for a user.
   *
   * @param userId the user ID
   * @return fine statistics
   */
  @GET
  @Path("/statistics/{userId}")
  public Response getFineStatistics(@PathParam("userId") Long userId) {
    Map<String, Object> statistics = enhancedFineService.calculateFineStatistics(userId);
    return Response.ok(statistics).build();
  }

  /**
   * Predicts fine amount for a loan.
   *
   * @param loanId the loan ID
   * @param futureDays the number of days in the future
   * @return the predicted fine amount
   */
  @GET
  @Path("/predict/{loanId}")
  public Response predictFineAmount(
      @PathParam("loanId") Long loanId, @QueryParam("futureDays") int futureDays) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    BigDecimal predictedAmount = enhancedFineService.predictFineAmount(loan, futureDays);
    return Response.ok().entity(new FineCalculationResponse(predictedAmount)).build();
  }

  /**
   * Calculates early payment discount.
   *
   * @param fineId the fine ID
   * @param request the early payment request
   * @return the discounted fine amount
   */
  @POST
  @Path("/{fineId}/early-payment-discount")
  public Response calculateEarlyPaymentDiscount(
      @PathParam("fineId") Long fineId, EarlyPaymentRequest request) {
    Fine fine = Fine.findById(fineId);
    if (fine == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Fine not found").build();
    }

    BigDecimal discountedAmount =
        enhancedFineService.calculateEarlyPaymentDiscount(fine, request.paymentDays);
    return Response.ok().entity(new FineCalculationResponse(discountedAmount)).build();
  }

  /**
   * Calculates late payment penalty.
   *
   * @param fineId the fine ID
   * @param request the late payment request
   * @return the penalized fine amount
   */
  @POST
  @Path("/{fineId}/late-payment-penalty")
  public Response calculateLatePaymentPenalty(
      @PathParam("fineId") Long fineId, LatePaymentRequest request) {
    Fine fine = Fine.findById(fineId);
    if (fine == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Fine not found").build();
    }

    BigDecimal penalizedAmount =
        enhancedFineService.calculateLatePaymentPenalty(fine, request.daysLate);
    return Response.ok().entity(new FineCalculationResponse(penalizedAmount)).build();
  }

  /**
   * Gets all enhanced fine calculations for a loan.
   *
   * @param loanId the loan ID
   * @return all fine calculations
   */
  @GET
  @Path("/loan/{loanId}/all-calculations")
  public Response getAllCalculations(@PathParam("loanId") Long loanId) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    EnhancedFineCalculationsResponse response = new EnhancedFineCalculationsResponse();
    response.enhancedFine = enhancedFineService.calculateEnhancedFine(loan);
    response.gracePeriodFine = enhancedFineService.calculateFineWithGracePeriod(loan);
    response.loyaltyDiscountFine =
        enhancedFineService.calculateFineWithLoyaltyDiscount(loan, loan.user);
    response.seasonalFine = enhancedFineService.calculateFineWithSeasonalAdjustments(loan);
    response.volumeDiscount = enhancedFineService.calculateVolumeDiscount(loan.user);

    return Response.ok(response).build();
  }

  /**
   * Compares different fine calculation methods.
   *
   * @param loanId the loan ID
   * @return comparison of different calculation methods
   */
  @GET
  @Path("/compare/{loanId}")
  public Response compareCalculations(@PathParam("loanId") Long loanId) {
    Loan loan = Loan.findById(loanId);
    if (loan == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("Loan not found").build();
    }

    FineComparisonResponse response = new FineComparisonResponse();
    response.standardFine =
        loan.getCurrentFine() != null ? loan.getCurrentFine().amount : BigDecimal.ZERO;
    response.enhancedFine = enhancedFineService.calculateEnhancedFine(loan);
    response.gracePeriodFine = enhancedFineService.calculateFineWithGracePeriod(loan);
    response.loyaltyDiscountFine =
        enhancedFineService.calculateFineWithLoyaltyDiscount(loan, loan.user);
    response.seasonalFine = enhancedFineService.calculateFineWithSeasonalAdjustments(loan);

    return Response.ok(response).build();
  }

  // Request/Response classes

  public static class InstallmentRequest {
    public int installmentCount;
  }

  public static class EarlyPaymentRequest {
    public int paymentDays;
  }

  public static class LatePaymentRequest {
    public int daysLate;
  }

  public static class FineCalculationResponse {
    public BigDecimal amount;

    public FineCalculationResponse(BigDecimal amount) {
      this.amount = amount;
    }
  }

  public static class DiscountResponse {
    public BigDecimal discountPercentage;

    public DiscountResponse(BigDecimal discountPercentage) {
      this.discountPercentage = discountPercentage;
    }
  }

  public static class InstallmentResponse {
    public BigDecimal installmentAmount;

    public InstallmentResponse(BigDecimal installmentAmount) {
      this.installmentAmount = installmentAmount;
    }
  }

  public static class EnhancedFineCalculationsResponse {
    public BigDecimal enhancedFine;
    public BigDecimal gracePeriodFine;
    public BigDecimal loyaltyDiscountFine;
    public BigDecimal seasonalFine;
    public BigDecimal volumeDiscount;
  }

  public static class FineComparisonResponse {
    public BigDecimal standardFine;
    public BigDecimal enhancedFine;
    public BigDecimal gracePeriodFine;
    public BigDecimal loyaltyDiscountFine;
    public BigDecimal seasonalFine;
  }
}
