package com.davonlibrary.resource;

import com.davonlibrary.entity.Report;
import com.davonlibrary.entity.Report.ReportStatus;
import com.davonlibrary.entity.Report.ReportType;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

/** REST resource for report management. */
@Path("/api/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {

  /**
   * Gets all reports.
   *
   * @return list of all reports
   */
  @GET
  public List<Report> getAllReports() {
    return Report.listAll();
  }

  /**
   * Gets a report by ID.
   *
   * @param reportId the report ID
   * @return the report if found
   */
  @GET
  @Path("/{reportId}")
  public Response getReport(@PathParam("reportId") Long reportId) {
    Report report = Report.findById(reportId);
    if (report == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(report).build();
  }

  /**
   * Creates a new report.
   *
   * @param request the report creation request
   * @return the created report
   */
  @POST
  public Response createReport(CreateReportRequest request) {
    Report report = new Report(request.type, request.title, request.description);

    if (request.startDate != null && request.endDate != null) {
      report.startDate = request.startDate;
      report.endDate = request.endDate;
    }

    report.generatedBy = request.generatedBy;
    report.persist();

    return Response.status(Response.Status.CREATED).entity(report).build();
  }

  /**
   * Gets reports by type.
   *
   * @param type the report type
   * @return list of reports
   */
  @GET
  @Path("/type/{type}")
  public Response getReportsByType(@PathParam("type") String type) {
    try {
      ReportType reportType = ReportType.valueOf(type.toUpperCase());
      List<Report> reports = Report.list("type", reportType);
      return Response.ok(reports).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid report type").build();
    }
  }

  /**
   * Gets reports by status.
   *
   * @param status the report status
   * @return list of reports
   */
  @GET
  @Path("/status/{status}")
  public Response getReportsByStatus(@PathParam("status") String status) {
    try {
      ReportStatus reportStatus = ReportStatus.valueOf(status.toUpperCase());
      List<Report> reports = Report.list("status", reportStatus);
      return Response.ok(reports).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid report status").build();
    }
  }

  /**
   * Gets completed reports.
   *
   * @return list of completed reports
   */
  @GET
  @Path("/completed")
  public Response getCompletedReports() {
    List<Report> reports = Report.list("status", ReportStatus.COMPLETED);
    return Response.ok(reports).build();
  }

  /**
   * Gets failed reports.
   *
   * @return list of failed reports
   */
  @GET
  @Path("/failed")
  public Response getFailedReports() {
    List<Report> reports = Report.list("status", ReportStatus.FAILED);
    return Response.ok(reports).build();
  }

  /**
   * Gets reports generated today.
   *
   * @return list of reports generated today
   */
  @GET
  @Path("/today")
  public Response getReportsGeneratedToday() {
    LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1);

    // BUG: Incorrect today query
    List<Report> reports =
        Report.list(
            "generatedDate >= ?1 AND generatedDate <= ?2", startOfDay, endOfDay.plusDays(1));
    return Response.ok(reports).build();
  }

  /**
   * Gets reports by date range.
   *
   * @param startDate the start date
   * @param endDate the end date
   * @return list of reports
   */
  @GET
  @Path("/date-range")
  public Response getReportsByDateRange(
      @QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
    try {
      LocalDateTime start = LocalDateTime.parse(startDate);
      LocalDateTime end = LocalDateTime.parse(endDate);

      // BUG: Incorrect date range query
      List<Report> reports =
          Report.list("generatedDate >= ?1 AND generatedDate <= ?2", start, end.plusDays(1));
      return Response.ok(reports).build();
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format").build();
    }
  }

  /**
   * Gets reports ready for download.
   *
   * @return list of reports ready for download
   */
  @GET
  @Path("/ready-for-download")
  public Response getReportsReadyForDownload() {
    // BUG: Incorrect ready check
    List<Report> reports =
        Report.list("status = ?1 AND filePath IS NOT NULL", ReportStatus.COMPLETED);
    return Response.ok(reports).build();
  }

  /**
   * Gets report statistics.
   *
   * @return report statistics
   */
  @GET
  @Path("/statistics")
  public Response getReportStatistics() {
    long totalReports = Report.count();
    long completedReports = Report.count("status", ReportStatus.COMPLETED);
    long failedReports = Report.count("status", ReportStatus.FAILED);
    long processingReports = Report.count("status", ReportStatus.PROCESSING);

    ReportStatisticsResponse response = new ReportStatisticsResponse();
    response.totalReports = totalReports;
    response.completedReports = completedReports;
    response.failedReports = failedReports;
    response.processingReports = processingReports;

    return Response.ok(response).build();
  }

  /**
   * Updates a report status.
   *
   * @param reportId the report ID
   * @param request the status update request
   * @return the updated report
   */
  @PUT
  @Path("/{reportId}/status")
  public Response updateReportStatus(
      @PathParam("reportId") Long reportId, UpdateStatusRequest request) {
    Report report = Report.findById(reportId);
    if (report == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    try {
      ReportStatus newStatus = ReportStatus.valueOf(request.status.toUpperCase());
      report.status = newStatus;

      if (request.filePath != null) {
        report.filePath = request.filePath;
      }
      if (request.fileSize != null) {
        report.fileSize = request.fileSize;
      }
      if (request.totalRecords != null) {
        report.totalRecords = request.totalRecords;
      }
      if (request.totalAmount != null) {
        report.totalAmount = request.totalAmount;
      }
      if (request.errorMessage != null) {
        report.errorMessage = request.errorMessage;
      }

      report.persist();
      return Response.ok(report).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid report status").build();
    }
  }

  /**
   * Deletes a report.
   *
   * @param reportId the report ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{reportId}")
  public Response deleteReport(@PathParam("reportId") Long reportId) {
    Report report = Report.findById(reportId);
    if (report == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    report.delete();
    return Response.noContent().build();
  }

  /**
   * Gets reports by generated by user.
   *
   * @param generatedBy the user who generated the reports
   * @return list of reports
   */
  @GET
  @Path("/generated-by/{generatedBy}")
  public Response getReportsByGeneratedBy(@PathParam("generatedBy") String generatedBy) {
    List<Report> reports = Report.list("generatedBy", generatedBy);
    return Response.ok(reports).build();
  }

  /**
   * Gets reports with errors.
   *
   * @return list of reports with errors
   */
  @GET
  @Path("/with-errors")
  public Response getReportsWithErrors() {
    List<Report> reports = Report.list("errorMessage IS NOT NULL");
    return Response.ok(reports).build();
  }

  /**
   * Gets overdue reports (taken too long to generate).
   *
   * @return list of overdue reports
   */
  @GET
  @Path("/overdue")
  public Response getOverdueReports() {
    LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
    // BUG: Incorrect overdue check
    List<Report> reports =
        Report.list(
            "status = ?1 AND generatedDate < ?2",
            ReportStatus.PROCESSING,
            thirtyMinutesAgo.plusMinutes(1));
    return Response.ok(reports).build();
  }

  // Request/Response classes

  public static class CreateReportRequest {
    public ReportType type;
    public String title;
    public String description;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public String generatedBy;
  }

  public static class UpdateStatusRequest {
    public String status;
    public String filePath;
    public Long fileSize;
    public Integer totalRecords;
    public java.math.BigDecimal totalAmount;
    public String errorMessage;
  }

  public static class ReportStatisticsResponse {
    public long totalReports;
    public long completedReports;
    public long failedReports;
    public long processingReports;
  }
}
