package com.davonlibrary.resource;

import com.davonlibrary.entity.Author;
import com.davonlibrary.entity.Book;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/** REST resource for managing books in the library system. */
@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

  /**
   * Gets all books.
   *
   * @return list of all books
   */
  @GET
  public List<Book> getAllBooks() {
    return Book.listAll();
  }

  /**
   * Gets a book by ID.
   *
   * @param id the book ID
   * @return the book if found
   */
  @GET
  @Path("/{id}")
  public Response getBook(@PathParam("id") Long id) {
    Book book = Book.findById(id);
    if (book == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(book).build();
  }

  /**
   * Creates a new book.
   *
   * @param book the book to create
   * @return the created book
   */
  @POST
  @Transactional
  public Response createBook(CreateBookRequest request) {
    // Find the author
    Author author = Author.findById(request.authorId);
    if (author == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Author not found").build();
    }

    Book book = new Book(request.title, request.isbn, author, request.totalCopies);
    book.persist();

    return Response.status(Response.Status.CREATED).entity(book).build();
  }

  /**
   * Updates an existing book.
   *
   * @param id the book ID
   * @param updatedBook the updated book data
   * @return the updated book
   */
  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateBook(@PathParam("id") Long id, UpdateBookRequest request) {
    Book book = Book.findById(id);
    if (book == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (request.title != null) {
      book.title = request.title;
    }
    if (request.isbn != null) {
      book.isbn = request.isbn;
    }
    if (request.totalCopies != null) {
      book.totalCopies = request.totalCopies;
      // Adjust available copies if necessary
      if (book.availableCopies > request.totalCopies) {
        book.availableCopies = request.totalCopies;
      }
    }

    book.persist();
    return Response.ok(book).build();
  }

  /**
   * Deletes a book.
   *
   * @param id the book ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteBook(@PathParam("id") Long id) {
    Book book = Book.findById(id);
    if (book == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    book.delete();
    return Response.noContent().build();
  }

  /**
   * Gets available books only.
   *
   * @return list of available books
   */
  @GET
  @Path("/available")
  public List<Book> getAvailableBooks() {
    return Book.list("availableCopies > 0");
  }

  /** Request DTO for creating a book. */
  public static class CreateBookRequest {
    public String title;
    public String isbn;
    public Long authorId;
    public Integer totalCopies = 1;
  }

  /** Request DTO for updating a book. */
  public static class UpdateBookRequest {
    public String title;
    public String isbn;
    public Integer totalCopies;
  }
}
