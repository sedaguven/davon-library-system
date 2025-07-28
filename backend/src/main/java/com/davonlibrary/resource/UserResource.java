package com.davonlibrary.resource;

import com.davonlibrary.entity.Library;
import com.davonlibrary.entity.LibraryMembership;
import com.davonlibrary.entity.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/** REST resource for managing users in the library system. */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  /**
   * Gets all users.
   *
   * @return list of all users
   */
  @GET
  public List<User> getAllUsers() {
    return User.listAll();
  }

  /**
   * Gets a user by ID.
   *
   * @param id the user ID
   * @return the user if found
   */
  @GET
  @Path("/{id}")
  public Response getUser(@PathParam("id") Long id) {
    User user = User.findById(id);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(user).build();
  }

  /**
   * Creates a new user.
   *
   * @param request the user creation request
   * @return the created user
   */
  @POST
  @Transactional
  public Response createUser(CreateUserRequest request) {
    User user = new User(request.firstName, request.lastName, request.email);
    user.persist();

    // Create library membership if libraryId is provided
    if (request.libraryId != null) {
      Library library = Library.findById(request.libraryId);
      if (library == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Library not found").build();
      }

      LibraryMembership membership = new LibraryMembership(user, library);
      membership.persist();
    }

    return Response.status(Response.Status.CREATED).entity(user).build();
  }

  /**
   * Updates an existing user.
   *
   * @param id the user ID
   * @param request the update request
   * @return the updated user
   */
  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateUser(@PathParam("id") Long id, UpdateUserRequest request) {
    User user = User.findById(id);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (request.firstName != null) {
      user.firstName = request.firstName;
    }
    if (request.lastName != null) {
      user.lastName = request.lastName;
    }
    if (request.email != null) {
      user.email = request.email;
    }
    if (request.libraryId != null) {
      Library library = Library.findById(request.libraryId);
      if (library != null) {
        // Check if user is already a member of this library
        if (!user.isMemberOf(library)) {
          // Create a new membership
          LibraryMembership membership = new LibraryMembership(user, library);
          membership.persist();
        }
      }
    }

    user.persist();
    return Response.ok(user).build();
  }

  /**
   * Deletes a user.
   *
   * @param id the user ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteUser(@PathParam("id") Long id) {
    User user = User.findById(id);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    user.delete();
    return Response.noContent().build();
  }

  /**
   * Gets current loans for a user.
   *
   * @param id the user ID
   * @return list of current loans
   */
  @GET
  @Path("/{id}/loans")
  public Response getUserLoans(@PathParam("id") Long id) {
    User user = User.findById(id);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(user.getCurrentLoans()).build();
  }

  /**
   * Gets active reservations for a user.
   *
   * @param id the user ID
   * @return list of active reservations
   */
  @GET
  @Path("/{id}/reservations")
  public Response getUserReservations(@PathParam("id") Long id) {
    User user = User.findById(id);
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(user.getActiveReservations()).build();
  }

  /** Request DTO for creating a user. */
  public static class CreateUserRequest {
    public String firstName;
    public String lastName;
    public String email;
    public Long libraryId;
  }

  /** Request DTO for updating a user. */
  public static class UpdateUserRequest {
    public String firstName;
    public String lastName;
    public String email;
    public Long libraryId;
  }
}
