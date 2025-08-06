package com.davonlibrary.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/** REST resource for managing authors in the library system. */
@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

  // All database methods removed. Only structure and DTOs remain.

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
