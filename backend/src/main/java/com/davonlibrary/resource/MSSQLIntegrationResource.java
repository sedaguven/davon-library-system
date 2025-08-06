package com.davonlibrary.resource;

import com.davonlibrary.service.MSSQLStoredProcedureService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** REST resource for testing and demonstrating MSSQL stored procedure integration. */
@Path("/api/mssql")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MSSQLIntegrationResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(MSSQLIntegrationResource.class);

  @Inject MSSQLStoredProcedureService storedProcedureService;

  /** Test the MSSQL connection and stored procedure access. */
  @GET
  @Path("/test")
  public Response testConnection() {
    try {
      Map<String, Object> result = storedProcedureService.testConnection();
      return Response.ok(result).build();
    } catch (Exception e) {
      LOGGER.error("Error testing MSSQL connection", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to test connection: " + e.getMessage()))
          .build();
    }
  }

  /** Borrow a book using the stored procedure. */
  @POST
  @Path("/borrow")
  public Response borrowBook(Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      Long bookCopyId = Long.valueOf(request.get("bookCopyId").toString());
      Integer loanPeriodDays =
          request.containsKey("loanPeriodDays")
              ? Integer.valueOf(request.get("loanPeriodDays").toString())
              : null;

      String result = storedProcedureService.borrowBook(userId, bookCopyId, loanPeriodDays);

      return Response.ok(
              Map.of(
                  "message", result,
                  "userId", userId,
                  "bookCopyId", bookCopyId,
                  "loanPeriodDays", loanPeriodDays != null ? loanPeriodDays : 14))
          .build();

    } catch (Exception e) {
      LOGGER.error("Error borrowing book", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to borrow book: " + e.getMessage()))
          .build();
    }
  }

  /** Return a book using the stored procedure. */
  @POST
  @Path("/return")
  public Response returnBook(Map<String, Object> request) {
    try {
      Long loanId = Long.valueOf(request.get("loanId").toString());

      String result = storedProcedureService.returnBook(loanId);

      return Response.ok(
              Map.of(
                  "message", result,
                  "loanId", loanId))
          .build();

    } catch (Exception e) {
      LOGGER.error("Error returning book", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to return book: " + e.getMessage()))
          .build();
    }
  }

  /** Search books using the stored procedure. */
  @GET
  @Path("/search")
  public Response searchBooks(
      @QueryParam("term") String searchTerm,
      @QueryParam("author") String authorName,
      @QueryParam("isbn") String isbn) {
    try {
      List<Map<String, Object>> results =
          storedProcedureService.searchBooks(searchTerm, authorName, isbn);

      return Response.ok(
              Map.of(
                  "books", results,
                  "count", results.size(),
                  "searchTerm", searchTerm,
                  "authorName", authorName,
                  "isbn", isbn))
          .build();

    } catch (Exception e) {
      LOGGER.error("Error searching books", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to search books: " + e.getMessage()))
          .build();
    }
  }

  /** Get overdue loans using stored procedure. */
  @GET
  @Path("/overdue")
  public Response getOverdueLoans() {
    try {
      List<Map<String, Object>> results = storedProcedureService.getOverdueLoans();

      return Response.ok(Map.of("overdueLoans", results, "count", results.size())).build();

    } catch (Exception e) {
      LOGGER.error("Error getting overdue loans", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to get overdue loans: " + e.getMessage()))
          .build();
    }
  }

  /** Get library statistics using stored procedure. */
  @GET
  @Path("/statistics")
  public Response getLibraryStatistics(@QueryParam("libraryId") Long libraryId) {
    try {
      Map<String, Object> stats = storedProcedureService.getLibraryStatistics(libraryId);

      return Response.ok(
              Map.of(
                  "statistics", stats,
                  "libraryId", libraryId))
          .build();

    } catch (Exception e) {
      LOGGER.error("Error getting library statistics", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to get library statistics: " + e.getMessage()))
          .build();
    }
  }

  /** Execute a custom stored procedure. */
  @POST
  @Path("/procedure/{procedureName}")
  public Response executeCustomProcedure(
      @PathParam("procedureName") String procedureName, List<Object> parameters) {
    try {
      List<Map<String, Object>> results =
          storedProcedureService.executeCustomProcedure(procedureName, parameters.toArray());

      return Response.ok(
              Map.of(
                  "procedureName", procedureName,
                  "parameters", parameters,
                  "results", results,
                  "count", results.size()))
          .build();

    } catch (Exception e) {
      LOGGER.error("Error executing custom procedure: {}", procedureName, e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to execute procedure: " + e.getMessage()))
          .build();
    }
  }

  /** Get available stored procedures. */
  @GET
  @Path("/procedures")
  public Response getAvailableProcedures() {
    try {
      Map<String, Object> procedures =
          Map.of(
              "availableProcedures",
              List.of(
                  Map.of(
                      "name", "sp_borrow_book",
                      "description", "Borrow a book for a user",
                      "parameters", List.of("userId", "bookCopyId", "loanPeriodDays")),
                  Map.of(
                      "name", "sp_return_book",
                      "description", "Return a borrowed book",
                      "parameters", List.of("loanId")),
                  Map.of(
                      "name", "sp_search_books",
                      "description", "Search books by various criteria",
                      "parameters", List.of("searchTerm", "authorName", "isbn")),
                  Map.of(
                      "name", "sp_get_overdue_loans",
                      "description", "Get all overdue loans",
                      "parameters", List.of()),
                  Map.of(
                      "name", "sp_get_library_statistics",
                      "description", "Get library statistics",
                      "parameters", List.of("libraryId"))),
              "totalCount",
              5);

      return Response.ok(procedures).build();

    } catch (Exception e) {
      LOGGER.error("Error getting available procedures", e);
      return Response.serverError()
          .entity(Map.of("error", "Failed to get available procedures: " + e.getMessage()))
          .build();
    }
  }
}
