package com.davonlibrary.resource;

import com.davonlibrary.service.LibraryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/library")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibraryResource {

  @Inject private LibraryService libraryService;

  @POST
  @Path("/borrow")
  public Response borrowBook(BorrowRequest request) {
    try {
      Object result = libraryService.borrowBook(request.getUserId(), request.getBookId());
      SuccessResponse response = new SuccessResponse(true, "Book borrowed successfully", "Loan");
      return Response.ok(response).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (IllegalStateException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("/reserve")
  public Response reserveBook(BorrowRequest request) {
    try {
      Object result = libraryService.reserveBook(request.getUserId(), request.getBookId());
      SuccessResponse response =
          new SuccessResponse(true, "Book reserved successfully", "Reservation");
      return Response.ok(response).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (IllegalStateException e) {
      return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    }
  }

  public static class BorrowRequest {
    private Long userId;
    private Long bookId;

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public Long getBookId() {
      return bookId;
    }

    public void setBookId(Long bookId) {
      this.bookId = bookId;
    }
  }

  public static class SuccessResponse {
    public boolean success;
    public String message;
    public String type;

    public SuccessResponse(boolean success, String message, String type) {
      this.success = success;
      this.message = message;
      this.type = type;
    }
  }
}
