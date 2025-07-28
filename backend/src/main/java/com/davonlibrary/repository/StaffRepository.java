package com.davonlibrary.repository;

import com.davonlibrary.entity.Staff;
import com.davonlibrary.entity.Staff.EmploymentStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/** Repository for Staff entity operations and queries. */
@ApplicationScoped
public class StaffRepository implements PanacheRepository<Staff> {

  /**
   * Finds a staff member by email.
   *
   * @param email the email address
   * @return the staff member if found
   */
  public Optional<Staff> findByEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  /**
   * Finds a staff member by employee ID.
   *
   * @param employeeId the employee ID
   * @return the staff member if found
   */
  public Optional<Staff> findByEmployeeId(String employeeId) {
    return find("employeeId", employeeId).firstResultOptional();
  }

  /**
   * Finds all active staff members.
   *
   * @return list of active staff
   */
  public List<Staff> findActive() {
    return list("employmentStatus = ?1", EmploymentStatus.ACTIVE);
  }

  /**
   * Finds staff members by library.
   *
   * @param libraryId the library ID
   * @return list of staff in the library
   */
  public List<Staff> findByLibrary(Long libraryId) {
    return list("library.id", libraryId);
  }

  /**
   * Finds active staff members by library.
   *
   * @param libraryId the library ID
   * @return list of active staff in the library
   */
  public List<Staff> findActiveByLibrary(Long libraryId) {
    return list("library.id = ?1 AND employmentStatus = ?2", libraryId, EmploymentStatus.ACTIVE);
  }

  /**
   * Finds staff members by employment status.
   *
   * @param status the employment status
   * @return list of staff with the specified status
   */
  public List<Staff> findByEmploymentStatus(EmploymentStatus status) {
    return list("employmentStatus", status);
  }

  /**
   * Finds staff members by position.
   *
   * @param position the position
   * @return list of staff with the specified position
   */
  public List<Staff> findByPosition(String position) {
    return list("LOWER(position) = LOWER(?1)", position);
  }

  /**
   * Finds staff members by department.
   *
   * @param department the department
   * @return list of staff in the specified department
   */
  public List<Staff> findByDepartment(String department) {
    return list("LOWER(department) = LOWER(?1)", department);
  }

  /**
   * Finds staff members by name containing search term.
   *
   * @param searchTerm the search term
   * @return list of staff matching the search
   */
  public List<Staff> findByNameContaining(String searchTerm) {
    String pattern = "%" + searchTerm.toLowerCase() + "%";
    return list("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1", pattern);
  }

  /**
   * Finds supervisors (staff members who have supervisees).
   *
   * @return list of staff who are supervisors
   */
  public List<Staff> findSupervisors() {
    return find("SELECT DISTINCT s.supervisor FROM Staff s WHERE s.supervisor IS NOT NULL").list();
  }

  /**
   * Finds staff members supervised by a specific supervisor.
   *
   * @param supervisorId the supervisor ID
   * @return list of staff supervised by the supervisor
   */
  public List<Staff> findBySupervisor(Long supervisorId) {
    return list("supervisor.id", supervisorId);
  }

  /**
   * Counts staff members by library.
   *
   * @param libraryId the library ID
   * @return number of staff in the library
   */
  public long countByLibrary(Long libraryId) {
    return count("library.id", libraryId);
  }

  /**
   * Counts active staff members by library.
   *
   * @param libraryId the library ID
   * @return number of active staff in the library
   */
  public long countActiveByLibrary(Long libraryId) {
    return count("library.id = ?1 AND employmentStatus = ?2", libraryId, EmploymentStatus.ACTIVE);
  }

  /**
   * Checks if a staff member exists by email.
   *
   * @param email the email address
   * @return true if staff member exists
   */
  public boolean existsByEmail(String email) {
    return count("email", email) > 0;
  }

  /**
   * Checks if a staff member exists by employee ID.
   *
   * @param employeeId the employee ID
   * @return true if staff member exists
   */
  public boolean existsByEmployeeId(String employeeId) {
    return count("employeeId", employeeId) > 0;
  }

  /**
   * Gets staff statistics for a library.
   *
   * @param libraryId the library ID
   * @return staff statistics
   */
  public StaffStats getStaffStats(Long libraryId) {
    long totalStaff = count("library.id", libraryId);
    long activeStaff =
        count("library.id = ?1 AND employmentStatus = ?2", libraryId, EmploymentStatus.ACTIVE);
    long onLeaveStaff =
        count("library.id = ?1 AND employmentStatus = ?2", libraryId, EmploymentStatus.ON_LEAVE);
    long terminatedStaff =
        count("library.id = ?1 AND employmentStatus = ?2", libraryId, EmploymentStatus.TERMINATED);

    return new StaffStats(totalStaff, activeStaff, onLeaveStaff, terminatedStaff);
  }

  /** Staff statistics DTO. */
  public static class StaffStats {
    public final long totalStaff;
    public final long activeStaff;
    public final long onLeaveStaff;
    public final long terminatedStaff;

    public StaffStats(long totalStaff, long activeStaff, long onLeaveStaff, long terminatedStaff) {
      this.totalStaff = totalStaff;
      this.activeStaff = activeStaff;
      this.onLeaveStaff = onLeaveStaff;
      this.terminatedStaff = terminatedStaff;
    }
  }
}
