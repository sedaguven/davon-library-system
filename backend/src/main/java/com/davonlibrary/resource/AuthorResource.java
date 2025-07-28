package com.davonlibrary.resource;

import com.davonlibrary.entity.Author;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/** REST resource for managing authors in the library system. */
@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

  /**
   * Gets all authors.
   *
   * @return list of all authors
   */
  @GET
  public List<Author> getAllAuthors() {
    return Author.listAll();
  }

  /**
   * Gets an author by ID.
   *
   * @param id the author ID
   * @return the author if found
   */
  @GET
  @Path("/{id}")
  public Response getAuthor(@PathParam("id") Long id) {
    Author author = Author.findById(id);
    if (author == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(author).build();
  }

  /**
   * Creates a new author.
   *
   * @param request the author creation request
   * @return the created author
   */
  @POST
  @Transactional
  public Response createAuthor(CreateAuthorRequest request) {
    Author author = new Author(request.firstName, request.lastName);
    author.persist();

    return Response.status(Response.Status.CREATED).entity(author).build();
  }

  /**
   * Updates an existing author.
   *
   * @param id the author ID
   * @param request the update request
   * @return the updated author
   */
  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateAuthor(@PathParam("id") Long id, UpdateAuthorRequest request) {
    Author author = Author.findById(id);
    if (author == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (request.firstName != null) {
      author.firstName = request.firstName;
    }
    if (request.lastName != null) {
      author.lastName = request.lastName;
    }

    author.persist();
    return Response.ok(author).build();
  }

  /**
   * Deletes an author.
   *
   * @param id the author ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteAuthor(@PathParam("id") Long id) {
    Author author = Author.findById(id);
    if (author == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    author.delete();
    return Response.noContent().build();
  }

  /** Request DTO for creating an author. */
  public static class CreateAuthorRequest {
    public String firstName;
    public String lastName;
  }

  /** Request DTO for updating an author. */
  public static class UpdateAuthorRequest {
    public String firstName;
    public String lastName;
  }
}
