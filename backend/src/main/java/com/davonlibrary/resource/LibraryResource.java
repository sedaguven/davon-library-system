package com.davonlibrary.resource;

import com.davonlibrary.entity.Library;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/** REST resource for managing libraries in the system. */
@Path("/libraries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibraryResource {

  /**
   * Gets all libraries.
   *
   * @return list of all libraries
   */
  @GET
  public List<Library> getAllLibraries() {
    return Library.listAll();
  }

  /**
   * Gets a library by ID.
   *
   * @param id the library ID
   * @return the library if found
   */
  @GET
  @Path("/{id}")
  public Response getLibrary(@PathParam("id") Long id) {
    Library library = Library.findById(id);
    if (library == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(library).build();
  }

  /**
   * Creates a new library.
   *
   * @param request the library creation request
   * @return the created library
   */
  @POST
  @Transactional
  public Response createLibrary(CreateLibraryRequest request) {
    Library library = new Library(request.name, request.address, request.city);

    library.persist();

    return Response.status(Response.Status.CREATED).entity(library).build();
  }

  /**
   * Updates an existing library.
   *
   * @param id the library ID
   * @param request the update request
   * @return the updated library
   */
  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateLibrary(@PathParam("id") Long id, UpdateLibraryRequest request) {
    Library library = Library.findById(id);
    if (library == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (request.name != null) {
      library.name = request.name;
    }
    if (request.address != null) {
      library.address = request.address;
    }
    if (request.city != null) {
      library.city = request.city;
    }

    library.persist();
    return Response.ok(library).build();
  }

  /**
   * Deletes a library.
   *
   * @param id the library ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteLibrary(@PathParam("id") Long id) {
    Library library = Library.findById(id);
    if (library == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    library.delete();
    return Response.noContent().build();
  }

  /**
   * Gets all libraries.
   *
   * @return list of all libraries
   */
  @GET
  @Path("/active")
  public List<Library> getActiveLibraries() {
    return Library.listAll();
  }

  /**
   * Gets available books for a specific library.
   *
   * @param id the library ID
   * @return list of available books
   */
  @GET
  @Path("/{id}/available-books")
  public Response getAvailableBooks(@PathParam("id") Long id) {
    Library library = Library.findById(id);
    if (library == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(library.getAvailableBooks()).build();
  }

  /**
   * Gets members of a specific library.
   *
   * @param id the library ID
   * @return list of library members
   */
  @GET
  @Path("/{id}/members")
  public Response getLibraryMembers(@PathParam("id") Long id) {
    Library library = Library.findById(id);
    if (library == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(library.getActiveMembers()).build();
  }

  /**
   * Gets staff of a specific library.
   *
   * @param id the library ID
   * @return list of library staff
   */
  @GET
  @Path("/{id}/staff")
  public Response getLibraryStaff(@PathParam("id") Long id) {
    Library library = Library.findById(id);
    if (library == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(library.staff).build();
  }

  /** Request DTO for creating a library. */
  public static class CreateLibraryRequest {
    public String name;
    public String address;
    public String city;
  }

  /** Request DTO for updating a library. */
  public static class UpdateLibraryRequest {
    public String name;
    public String address;
    public String city;
  }
}
