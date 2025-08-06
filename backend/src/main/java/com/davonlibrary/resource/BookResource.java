package com.davonlibrary.resource;

import com.davonlibrary.entity.Book;
import com.davonlibrary.repository.BookRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/** REST resource for managing books in the library system. */
@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

  @Inject BookRepository bookRepository;

  @GET
  public Response getAllBooks() {
    try {
      List<Book> books = bookRepository.listAll();
      long total = bookRepository.count();

      // Convert to DTO format for frontend
      List<BookDTO> bookDTOs = books.stream().map(this::convertToDTO).collect(Collectors.toList());

      // Create response object
      BookListResponse response = new BookListResponse();
      response.books = bookDTOs;
      response.total = total;

      return Response.ok(response).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving books: " + e.getMessage())
          .build();
    }
  }

  @GET
  @Path("/{id}")
  public Response getBookById(@PathParam("id") Long id) {
    try {
      Book book = bookRepository.findById(id);
      if (book != null) {
        return Response.ok(convertToDTO(book)).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("Book not found with id: " + id)
            .build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving book: " + e.getMessage())
          .build();
    }
  }

  private BookDTO convertToDTO(Book book) {
    BookDTO dto = new BookDTO();
    dto.id = book.id;
    dto.title = book.title;
    dto.isbn = book.isbn;
    dto.availableCopies = book.availableCopies;
    dto.totalCopies = book.totalCopies;

    // Get author name
    if (book.author != null) {
      dto.author = book.author.firstName + " " + book.author.lastName;
    } else {
      dto.author = "Unknown Author";
    }

    return dto;
  }

  /** Response DTO for books */
  public static class BookDTO {
    public Long id;
    public String title;
    public String isbn;
    public String author;
    public Integer availableCopies;
    public Integer totalCopies;
  }

  /** Response DTO for a list of books. */
  public static class BookListResponse {
    public List<BookDTO> books;
    public long total;
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
