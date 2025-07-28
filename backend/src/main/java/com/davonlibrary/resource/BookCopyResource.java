package com.davonlibrary.resource;

import com.davonlibrary.entity.Book;
import com.davonlibrary.entity.BookCopy;
import com.davonlibrary.entity.Library;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/** REST resource for managing book copies in the library system. */
@Path("/book-copies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookCopyResource {

  /**
   * Gets all book copies.
   *
   * @return list of all book copies
   */
  @GET
  public List<BookCopy> getAllBookCopies() {
    return BookCopy.listAll();
  }

  /**
   * Gets a book copy by ID.
   *
   * @param id the book copy ID
   * @return the book copy if found
   */
  @GET
  @Path("/{id}")
  public Response getBookCopy(@PathParam("id") Long id) {
    BookCopy bookCopy = BookCopy.findById(id);
    if (bookCopy == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(bookCopy).build();
  }

  /**
   * Creates a new book copy.
   *
   * @param request the book copy creation request
   * @return the created book copy
   */
  @POST
  @Transactional
  public Response createBookCopy(CreateBookCopyRequest request) {
    // Find the book
    Book book = Book.findById(request.bookId);
    if (book == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Book not found").build();
    }

    // Find the library
    Library library = Library.findById(request.libraryId);
    if (library == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Library not found").build();
    }

    BookCopy bookCopy = new BookCopy(book, library, request.barcode, request.location);
    bookCopy.persist();

    // Update book's total copies count
    book.totalCopies = (book.totalCopies != null ? book.totalCopies : 0) + 1;
    book.availableCopies = (book.availableCopies != null ? book.availableCopies : 0) + 1;
    book.persist();

    return Response.status(Response.Status.CREATED).entity(bookCopy).build();
  }

  /**
   * Updates an existing book copy.
   *
   * @param id the book copy ID
   * @param request the update request
   * @return the updated book copy
   */
  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateBookCopy(@PathParam("id") Long id, UpdateBookCopyRequest request) {
    BookCopy bookCopy = BookCopy.findById(id);
    if (bookCopy == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (request.barcode != null) {
      bookCopy.barcode = request.barcode;
    }
    if (request.location != null) {
      bookCopy.location = request.location;
    }
    if (request.status != null) {
      bookCopy.status = request.status;
      bookCopy.isAvailable = (request.status == BookCopy.BookCopyStatus.AVAILABLE);
    }
    if (request.notes != null) {
      bookCopy.notes = request.notes;
    }

    bookCopy.persist();
    return Response.ok(bookCopy).build();
  }

  /**
   * Deletes a book copy.
   *
   * @param id the book copy ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteBookCopy(@PathParam("id") Long id) {
    BookCopy bookCopy = BookCopy.findById(id);
    if (bookCopy == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    // Update book's total copies count
    if (bookCopy.book != null) {
      Book book = bookCopy.book;
      book.totalCopies = Math.max(0, (book.totalCopies != null ? book.totalCopies : 1) - 1);
      if (bookCopy.isAvailable) {
        book.availableCopies =
            Math.max(0, (book.availableCopies != null ? book.availableCopies : 1) - 1);
      }
      book.persist();
    }

    bookCopy.delete();
    return Response.noContent().build();
  }

  /**
   * Gets available book copies only.
   *
   * @return list of available book copies
   */
  @GET
  @Path("/available")
  public List<BookCopy> getAvailableBookCopies() {
    return BookCopy.list("isAvailable = true");
  }

  /**
   * Gets book copies by library.
   *
   * @param libraryId the library ID
   * @return list of book copies in the library
   */
  @GET
  @Path("/by-library/{libraryId}")
  public List<BookCopy> getBookCopiesByLibrary(@PathParam("libraryId") Long libraryId) {
    return BookCopy.list("library.id = ?1", libraryId);
  }

  /**
   * Gets book copies by book.
   *
   * @param bookId the book ID
   * @return list of book copies for the book
   */
  @GET
  @Path("/by-book/{bookId}")
  public List<BookCopy> getBookCopiesByBook(@PathParam("bookId") Long bookId) {
    return BookCopy.list("book.id = ?1", bookId);
  }

  /**
   * Marks a book copy as damaged and sends it to maintenance.
   *
   * @param id the book copy ID
   * @param request the maintenance request
   * @return response indicating success or failure
   */
  @POST
  @Path("/{id}/maintenance")
  @Transactional
  public Response sendToMaintenance(@PathParam("id") Long id, MaintenanceRequest request) {
    BookCopy bookCopy = BookCopy.findById(id);
    if (bookCopy == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    bookCopy.sendToMaintenance(request.reason);
    bookCopy.persist();

    return Response.ok(new MaintenanceResponse(true, "Book copy sent to maintenance")).build();
  }

  /** Request DTO for creating a book copy. */
  public static class CreateBookCopyRequest {
    public Long bookId;
    public Long libraryId;
    public String barcode;
    public String location;
  }

  /** Request DTO for updating a book copy. */
  public static class UpdateBookCopyRequest {
    public String barcode;
    public String location;
    public BookCopy.BookCopyStatus status;
    public String notes;
  }

  /** Request DTO for maintenance operations. */
  public static class MaintenanceRequest {
    public String reason;
  }

  /** Response DTO for maintenance operations. */
  public static class MaintenanceResponse {
    public boolean success;
    public String message;

    public MaintenanceResponse(boolean success, String message) {
      this.success = success;
      this.message = message;
    }
  }
}
