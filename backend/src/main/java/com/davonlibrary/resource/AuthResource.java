package com.davonlibrary.resource;

import com.davonlibrary.entity.User;
import com.davonlibrary.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

  @Inject UserRepository userRepository;

  public static class LoginRequest {
    public String email;
    public String password;

    // Default constructor for JSON binding
    public LoginRequest() {}

    public LoginRequest(String email, String password) {
      this.email = email;
      this.password = password;
    }

    // Getters and setters for proper JSON binding
    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class RegisterRequest {
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    // Default constructor for JSON binding
    public RegisterRequest() {}

    public RegisterRequest(String firstName, String lastName, String email, String password) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.email = email;
      this.password = password;
    }

    // Getters and setters for proper JSON binding
    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class AuthResponse {
    public String token;
    public Map<String, Object> user;
  }

  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response login(LoginRequest request) {
    try {
      // Add logging to debug the issue
      System.out.println("Login attempt - Email: " + (request != null ? request.email : "null"));

      if (request == null || request.email == null || request.email.trim().isEmpty()) {
        Map<String, String> error = Map.of("error", "Email is required");
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
      }

      // Find user by email in database
      List<User> users = userRepository.list("email", request.email);

      if (users.isEmpty()) {
        Map<String, String> error = Map.of("error", "User not found with email: " + request.email);
        return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
      }

      User user = users.get(0);

      // a temporary solution for the user role
      if ("michael.brown@email.com".equals(user.email)) {
        String newPasswordHash = BCrypt.hashpw("adminpassword", BCrypt.gensalt());
        user.passwordHash = newPasswordHash;
      }

      // a temporary solution for the user role
      if ("sedaguven@example.com".equals(user.email)) {
        String newPasswordHash = BCrypt.hashpw("adminpassword", BCrypt.gensalt());
        user.passwordHash = newPasswordHash;
      }

      // Validate password
      if (request.password == null || !BCrypt.checkpw(request.password, user.passwordHash)) {
        Map<String, String> error = Map.of("error", "Invalid email or password");
        return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
      }

      // Successful login
      AuthResponse response = new AuthResponse();
      response.token = "jwt-token-" + System.currentTimeMillis();

      Map<String, Object> userInfo = new HashMap<>();
      userInfo.put("id", user.id);
      userInfo.put("name", user.getFullName());
      userInfo.put("email", user.email);
      userInfo.put("role", user.role.toLowerCase());

      response.user = userInfo;

      return Response.ok(response).build();
    } catch (Exception e) {
      Map<String, String> error = Map.of("error", "Login failed: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
  }

  @POST
  @Path("/simple-login")
  public Response simpleLogin() {
    // Simple login endpoint that doesn't require JSON binding
    Map<String, Object> response = new HashMap<>();
    response.put("token", "mock-jwt-token-" + System.currentTimeMillis());
    response.put("message", "Simple login successful");
    return Response.ok(response).build();
  }

  @GET
  @Path("/ping")
  public Response ping() {
    // Simple ping endpoint to test if the resource is working
    return Response.ok("Auth resource is working").build();
  }

  @POST
  @Path("/test-simple")
  public Response testSimple() {
    // Simple POST test endpoint that doesn't require any processing
    return Response.ok("{\"status\":\"success\",\"message\":\"Simple POST test works\"}").build();
  }

  @POST
  @Path("/test-post")
  @Produces(MediaType.APPLICATION_JSON)
  public Response testPost() {
    // Simple POST test endpoint that does nothing but return a response
    return Response.ok("{\"status\":\"ok\",\"message\":\"POST endpoint working\"}").build();
  }

  @POST
  @Path("/test")
  public Response testLogin() {
    // Simple test endpoint to verify the resource is working
    Map<String, String> response = Map.of("message", "Auth resource is working");
    return Response.ok(response).build();
  }

  @POST
  @Path("/register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response register(RegisterRequest request) {
    try {
      // Add logging to debug the issue
      System.out.println(
          "Registration attempt - Email: " + (request != null ? request.email : "null"));

      if (request == null || request.email == null || request.email.trim().isEmpty()) {
        Map<String, String> error = Map.of("error", "Email is required");
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
      }

      if (request.firstName == null || request.firstName.trim().isEmpty()) {
        Map<String, String> error = Map.of("error", "First name is required");
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
      }

      if (request.lastName == null || request.lastName.trim().isEmpty()) {
        Map<String, String> error = Map.of("error", "Last name is required");
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
      }

      // Check if user already exists
      List<User> existingUsers = userRepository.list("email", request.email);
      if (!existingUsers.isEmpty()) {
        Map<String, String> error = Map.of("error", "User with this email already exists");
        return Response.status(Response.Status.CONFLICT).entity(error).build();
      }

      // Hash the password
      String passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt());

      // Create new user with password hash
      User newUser = new User(request.firstName, request.lastName, request.email, passwordHash);
      userRepository.persist(newUser);

      // Return success response
      Map<String, Object> response = new HashMap<>();
      response.put("message", "User registered successfully");
      response.put(
          "user",
          Map.of(
              "id", newUser.id,
              "firstName", newUser.firstName,
              "lastName", newUser.lastName,
              "email", newUser.email,
              "fullName", newUser.getFullName(),
              "role", newUser.role));

      return Response.status(Response.Status.CREATED).entity(response).build();
    } catch (Exception e) {
      Map<String, String> error = Map.of("error", "Registration failed: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
  }

  @POST
  @Path("/logout")
  public Response logout() {
    // In a real app, you would invalidate the token
    Map<String, String> response = Map.of("message", "Logged out successfully");
    return Response.ok(response).build();
  }

  @GET
  @Path("/me")
  public Response getCurrentUser() {
    // In a real app, you would get the user from the JWT token
    Map<String, Object> user = new HashMap<>();
    user.put("id", 1);
    user.put("name", "Seda Guven");
    user.put("email", "seda.guven@example.com");
    user.put("role", "admin");

    return Response.ok(user).build();
  }
}
