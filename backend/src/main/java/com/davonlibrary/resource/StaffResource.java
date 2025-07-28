package com.davonlibrary.resource;

import com.davonlibrary.entity.Library;
import com.davonlibrary.entity.Staff;
import com.davonlibrary.entity.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/** REST resource for managing staff in the library system. */
@Path("/staff")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StaffResource {

  /**
   * Gets all staff members.
   *
   * @return list of all staff members
   */
  @GET
  public List<Staff> getAllStaff() {
    return Staff.listAll();
  }

  /**
   * Gets a staff member by ID.
   *
   * @param id the staff ID
   * @return the staff member if found
   */
  @GET
  @Path("/{id}")
  public Response getStaff(@PathParam("id") Long id) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(staff).build();
  }

  /**
   * Creates a new staff member.
   *
   * @param request the staff creation request
   * @return the created staff member
   */
  @POST
  @Transactional
  public Response createStaff(CreateStaffRequest request) {
    // Find the library
    Library library = Library.findById(request.libraryId);
    if (library == null) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Library not found").build();
    }

    Staff staff =
        new Staff(request.firstName, request.lastName, request.email, request.position, library);

    if (request.department != null) {
      staff.department = request.department;
    }
    if (request.employeeId != null) {
      staff.employeeId = request.employeeId;
    }
    if (request.supervisorId != null) {
      Staff supervisor = Staff.findById(request.supervisorId);
      if (supervisor != null) {
        staff.supervisor = supervisor;
      }
    }

    staff.persist();

    return Response.status(Response.Status.CREATED).entity(staff).build();
  }

  /**
   * Updates an existing staff member.
   *
   * @param id the staff ID
   * @param request the update request
   * @return the updated staff member
   */
  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateStaff(@PathParam("id") Long id, UpdateStaffRequest request) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (request.firstName != null) {
      staff.firstName = request.firstName;
    }
    if (request.lastName != null) {
      staff.lastName = request.lastName;
    }
    if (request.email != null) {
      staff.email = request.email;
    }
    if (request.position != null) {
      staff.position = request.position;
    }
    if (request.department != null) {
      staff.department = request.department;
    }
    if (request.employeeId != null) {
      staff.employeeId = request.employeeId;
    }
    if (request.employmentStatus != null) {
      staff.employmentStatus = request.employmentStatus;
    }
    if (request.supervisorId != null) {
      Staff supervisor = Staff.findById(request.supervisorId);
      if (supervisor != null) {
        staff.supervisor = supervisor;
      }
    }

    staff.persist();
    return Response.ok(staff).build();
  }

  /**
   * Deletes a staff member.
   *
   * @param id the staff ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteStaff(@PathParam("id") Long id) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    staff.delete();
    return Response.noContent().build();
  }

  /**
   * Gets active staff members only.
   *
   * @return list of active staff members
   */
  @GET
  @Path("/active")
  public List<Staff> getActiveStaff() {
    return Staff.list("employmentStatus = ?1", Staff.EmploymentStatus.ACTIVE);
  }

  /**
   * Gets staff members by library.
   *
   * @param libraryId the library ID
   * @return list of staff members in the library
   */
  @GET
  @Path("/by-library/{libraryId}")
  public List<Staff> getStaffByLibrary(@PathParam("libraryId") Long libraryId) {
    return Staff.list("library.id = ?1", libraryId);
  }

  /**
   * Staff member searches for books.
   *
   * @param id the staff ID
   * @param title the book title to search for
   * @return list of books matching the search
   */
  @GET
  @Path("/{id}/search-books")
  public Response searchBooks(@PathParam("id") Long id, @QueryParam("title") String title) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    if (title == null || title.trim().isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Title parameter is required")
          .build();
    }

    List<com.davonlibrary.entity.Book> books = staff.searchBooks(title);
    return Response.ok(books).build();
  }

  /**
   * Staff member checks book availability.
   *
   * @param id the staff ID
   * @param bookId the book ID to check
   * @return availability status
   */
  @GET
  @Path("/{id}/check-availability/{bookId}")
  public Response checkBookAvailability(
      @PathParam("id") Long id, @PathParam("bookId") Long bookId) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    boolean isAvailable = staff.checkAvailability(bookId);
    return Response.ok(new AvailabilityResponse(isAvailable)).build();
  }

  /**
   * Staff member processes book return.
   *
   * @param id the staff ID
   * @param request the return request
   * @return success status
   */
  @POST
  @Path("/{id}/process-return")
  @Transactional
  public Response processReturn(@PathParam("id") Long id, ProcessReturnRequest request) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    boolean success = staff.processReturn(request.bookCopyId);

    if (success) {
      return Response.ok(new ProcessResponse(true, "Book returned successfully")).build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(new ProcessResponse(false, "Failed to process return"))
          .build();
    }
  }

  /**
   * Staff member registers a new user.
   *
   * @param id the staff ID
   * @param request the user registration request
   * @return registration status
   */
  @POST
  @Path("/{id}/register-user")
  @Transactional
  public Response registerUser(@PathParam("id") Long id, RegisterUserRequest request) {
    Staff staff = Staff.findById(id);
    if (staff == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    User newUser = new User(request.firstName, request.lastName, request.email, staff.library);
    boolean success = staff.registerUser(newUser);

    if (success) {
      return Response.status(Response.Status.CREATED).entity(newUser).build();
    } else {
      return Response.status(Response.Status.BAD_REQUEST).entity("Failed to register user").build();
    }
  }

  /** Request DTO for creating a staff member. */
  public static class CreateStaffRequest {
    public String firstName;
    public String lastName;
    public String email;
    public String position;
    public String department;
    public String employeeId;
    public Long libraryId;
    public Long supervisorId;
  }

  /** Request DTO for updating a staff member. */
  public static class UpdateStaffRequest {
    public String firstName;
    public String lastName;
    public String email;
    public String position;
    public String department;
    public String employeeId;
    public Staff.EmploymentStatus employmentStatus;
    public Long supervisorId;
  }

  /** Request DTO for processing book return. */
  public static class ProcessReturnRequest {
    public Long bookCopyId;
  }

  /** Request DTO for user registration. */
  public static class RegisterUserRequest {
    public String firstName;
    public String lastName;
    public String email;
  }

  /** Response DTO for availability check. */
  public static class AvailabilityResponse {
    public boolean available;

    public AvailabilityResponse(boolean available) {
      this.available = available;
    }
  }

  /** Response DTO for process operations. */
  public static class ProcessResponse {
    public boolean success;
    public String message;

    public ProcessResponse(boolean success, String message) {
      this.success = success;
      this.message = message;
    }
  }
}
