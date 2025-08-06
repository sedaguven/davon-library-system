package com.davonlibrary.resource;

import com.davonlibrary.service.PasswordUpdateService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/api/password-update")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PasswordUpdateResource {

  @Inject PasswordUpdateService passwordUpdateService;

  @POST
  @Path("/assign-passwords")
  public Response assignPasswordsToExistingUsers() {
    try {
      Map<String, String> results = passwordUpdateService.assignPasswordsToExistingUsers();
      return Response.ok(results).build();
    } catch (Exception e) {
      Map<String, String> error = Map.of("error", "Failed to assign passwords: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
  }

  @GET
  @Path("/current-passwords")
  public Response getCurrentUserPasswords() {
    try {
      Map<String, String> results = passwordUpdateService.getCurrentUserPasswords();
      return Response.ok(results).build();
    } catch (Exception e) {
      Map<String, String> error = Map.of("error", "Failed to get passwords: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
  }
}
