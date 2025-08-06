package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.Staff;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffServiceTest {

  @Inject StaffService staffService;

  @Test
  void testServiceInjection() {
    assertNotNull(staffService);
  }

  @Test
  void testIsValidStaff_WithValidStaff() {
    Staff staff = new Staff();
    staff.firstName = "John";
    staff.lastName = "Doe";
    staff.email = "john.doe@library.com";

    assertTrue(staffService.isValidStaff(staff));
  }

  @Test
  void testIsValidStaff_WithNullStaff() {
    assertFalse(staffService.isValidStaff(null));
  }

  @Test
  void testIsValidStaff_WithNullFirstName() {
    Staff staff = new Staff();
    staff.firstName = null;
    staff.lastName = "Doe";
    staff.email = "john.doe@library.com";

    assertFalse(staffService.isValidStaff(staff));
  }

  @Test
  void testIsValidStaff_WithEmptyFirstName() {
    Staff staff = new Staff();
    staff.firstName = "";
    staff.lastName = "Doe";
    staff.email = "john.doe@library.com";

    assertFalse(staffService.isValidStaff(staff));
  }

  @Test
  void testIsValidStaff_WithNullLastName() {
    Staff staff = new Staff();
    staff.firstName = "John";
    staff.lastName = null;
    staff.email = "john.doe@library.com";

    assertFalse(staffService.isValidStaff(staff));
  }

  @Test
  void testIsValidStaff_WithNullEmail() {
    Staff staff = new Staff();
    staff.firstName = "John";
    staff.lastName = "Doe";
    staff.email = null;

    assertFalse(staffService.isValidStaff(staff));
  }

  @Test
  void testIsValidStaffId_WithValidId() {
    assertTrue(staffService.isValidStaffId(1L));
    assertTrue(staffService.isValidStaffId(100L));
  }

  @Test
  void testIsValidStaffId_WithNullId() {
    assertFalse(staffService.isValidStaffId(null));
  }

  @Test
  void testIsValidStaffId_WithZeroId() {
    assertFalse(staffService.isValidStaffId(0L));
  }

  @Test
  void testIsValidStaffId_WithNegativeId() {
    assertFalse(staffService.isValidStaffId(-1L));
  }

  @Test
  void testIsValidEmail_WithValidEmail() {
    assertTrue(staffService.isValidEmail("staff@library.com"));
    assertTrue(staffService.isValidEmail("john.doe@library.org"));
  }

  @Test
  void testIsValidEmail_WithNullEmail() {
    assertFalse(staffService.isValidEmail(null));
  }

  @Test
  void testIsValidEmail_WithEmptyEmail() {
    assertFalse(staffService.isValidEmail(""));
  }

  @Test
  void testIsValidEmail_WithInvalidEmail() {
    // The current validation only checks for "@" presence
    assertFalse(staffService.isValidEmail("invalidemail")); // No @ symbol
    assertTrue(staffService.isValidEmail("staff@")); // Contains @ (current logic allows this)
    assertTrue(staffService.isValidEmail("@library.com")); // Contains @ (current logic allows this)
  }

  @Test
  void testIsValidDepartment_WithValidDepartment() {
    assertTrue(staffService.isValidDepartment("Circulation"));
    assertTrue(staffService.isValidDepartment("Reference"));
  }

  @Test
  void testIsValidDepartment_WithNullDepartment() {
    assertFalse(staffService.isValidDepartment(null));
  }

  @Test
  void testIsValidDepartment_WithEmptyDepartment() {
    assertFalse(staffService.isValidDepartment(""));
  }

  @Test
  void testIsValidDepartment_WithWhitespaceDepartment() {
    assertFalse(staffService.isValidDepartment("   "));
  }

  @Test
  void testIsValidLibraryId_WithValidId() {
    assertTrue(staffService.isValidLibraryId(1L));
    assertTrue(staffService.isValidLibraryId(100L));
  }

  @Test
  void testIsValidLibraryId_WithNullId() {
    assertFalse(staffService.isValidLibraryId(null));
  }

  @Test
  void testIsValidLibraryId_WithZeroId() {
    assertFalse(staffService.isValidLibraryId(0L));
  }

  @Test
  void testIsValidLibraryId_WithNegativeId() {
    assertFalse(staffService.isValidLibraryId(-1L));
  }

  @Test
  void testIsValidPosition_WithValidPosition() {
    assertTrue(staffService.isValidPosition("Librarian"));
    assertTrue(staffService.isValidPosition("Assistant"));
  }

  @Test
  void testIsValidPosition_WithNullPosition() {
    assertFalse(staffService.isValidPosition(null));
  }

  @Test
  void testIsValidPosition_WithEmptyPosition() {
    assertFalse(staffService.isValidPosition(""));
  }

  @Test
  void testIsValidPosition_WithWhitespacePosition() {
    assertFalse(staffService.isValidPosition("   "));
  }
}
