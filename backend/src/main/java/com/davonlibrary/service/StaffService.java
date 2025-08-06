package com.davonlibrary.service;

import com.davonlibrary.entity.Staff;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StaffService {

  /**
   * Validates if a staff object is valid.
   *
   * @param staff the staff to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidStaff(Staff staff) {
    return staff != null
        && staff.firstName != null
        && !staff.firstName.trim().isEmpty()
        && staff.lastName != null
        && !staff.lastName.trim().isEmpty()
        && staff.email != null
        && !staff.email.trim().isEmpty();
  }

  /**
   * Validates if a staff ID is valid.
   *
   * @param id the staff ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidStaffId(Long id) {
    return id != null && id > 0;
  }

  /**
   * Validates if an email is valid.
   *
   * @param email the email to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidEmail(String email) {
    return email != null && !email.trim().isEmpty() && email.contains("@");
  }

  /**
   * Validates if a position is valid.
   *
   * @param position the position to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidPosition(String position) {
    return position != null && !position.trim().isEmpty();
  }

  /**
   * Validates if a department is valid.
   *
   * @param department the department to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidDepartment(String department) {
    return department != null && !department.trim().isEmpty();
  }

  /**
   * Validates if a library ID is valid.
   *
   * @param libraryId the library ID to validate
   * @return true if valid, false otherwise
   */
  public boolean isValidLibraryId(Long libraryId) {
    return libraryId != null && libraryId > 0;
  }
}
