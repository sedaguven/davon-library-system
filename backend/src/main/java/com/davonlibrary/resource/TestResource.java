package com.davonlibrary.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestResource {

  @GET
  @Path("/ping")
  public Response ping() {
    return Response.ok("Test resource is working").build();
  }

  @POST
  @Path("/simple")
  public Response simplePost() {
    return Response.ok("{\"status\":\"success\",\"message\":\"Simple POST works\"}").build();
  }

  @POST
  @Path("/with-body")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response postWithBody(String body) {
    return Response.ok("{\"status\":\"success\",\"received\":\"" + body + "\"}").build();
  }
}
