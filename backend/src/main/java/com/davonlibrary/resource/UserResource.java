package com.davonlibrary.resource;

import com.davonlibrary.entity.User;
import com.davonlibrary.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/** REST resource for managing users in the library system. */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

  @Inject UserRepository userRepository;

  @GET
  @Path("/profile")
  public Response getProfile() {
    // In a real application, you would get the user from the security context (e.g., JWT token)
    // For now, we'll return a mock user
    User mockUser = new User("Seda", "Guven", "seda.guven@example.com");
    mockUser.id = 1L;
    mockUser.role = "admin";
    return Response.ok(convertToDTO(mockUser)).build();
  }

  @GET
  public Response getAllUsers() {
    try {
      List<User> users = userRepository.listAll();
      long total = userRepository.count();

      // Convert to DTO format for frontend
      List<UserDTO> userDTOs = users.stream().map(this::convertToDTO).collect(Collectors.toList());

      // Create response object
      UserListResponse response = new UserListResponse();
      response.users = userDTOs;
      response.total = total;

      return Response.ok(response).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving users: " + e.getMessage())
          .build();
    }
  }

  @GET
  @Path("/{id}")
  public Response getUserById(@PathParam("id") Long id) {
    try {
      User user = userRepository.findById(id);
      if (user != null) {
        return Response.ok(convertToDTO(user)).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("User not found with id: " + id)
            .build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error retrieving user: " + e.getMessage())
          .build();
    }
  }

  private UserDTO convertToDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.id = user.id;
    dto.firstName = user.firstName;
    dto.lastName = user.lastName;
    dto.email = user.email;
    dto.fullName = user.getFullName();
    dto.role = user.role != null ? user.role.toLowerCase() : "user";
    return dto;
  }

  /** Response DTO for users */
  public static class UserDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public String fullName;
    public String role;
  }

  /** Response DTO for a list of users. */
  public static class UserListResponse {
    public List<UserDTO> users;
    public long total;
  }

  /** Request DTO for creating users */
  public static class CreateUserRequest {
    public String firstName;
    public String lastName;
    public String email;
    public Long libraryId;
  }

  /** Request DTO for updating users */
  public static class UpdateUserRequest {
    public String firstName;
    public String lastName;
    public String email;
  }
}
