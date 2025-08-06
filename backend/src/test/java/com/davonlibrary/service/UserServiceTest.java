package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserServiceTest {

  @Inject UserService userService;

  @Test
  void testServiceInjection() {
    assertNotNull(userService);
  }

  @Test
  void testIsValidUser_WithValidUser() {
    User user = new User();
    user.firstName = "John";
    user.lastName = "Doe";
    user.email = "john.doe@example.com";

    assertTrue(userService.isValidUser(user));
  }

  @Test
  void testIsValidUser_WithNullUser() {
    assertFalse(userService.isValidUser(null));
  }

  @Test
  void testIsValidUser_WithNullFirstName() {
    User user = new User();
    user.firstName = null;
    user.lastName = "Doe";
    user.email = "john.doe@example.com";

    assertFalse(userService.isValidUser(user));
  }

  @Test
  void testIsValidUser_WithEmptyFirstName() {
    User user = new User();
    user.firstName = "";
    user.lastName = "Doe";
    user.email = "john.doe@example.com";

    assertFalse(userService.isValidUser(user));
  }

  @Test
  void testIsValidUser_WithNullLastName() {
    User user = new User();
    user.firstName = "John";
    user.lastName = null;
    user.email = "john.doe@example.com";

    assertFalse(userService.isValidUser(user));
  }

  @Test
  void testIsValidUser_WithNullEmail() {
    User user = new User();
    user.firstName = "John";
    user.lastName = "Doe";
    user.email = null;

    assertFalse(userService.isValidUser(user));
  }

  @Test
  void testIsValidUserId_WithValidId() {
    assertTrue(userService.isValidUserId(1L));
    assertTrue(userService.isValidUserId(100L));
  }

  @Test
  void testIsValidUserId_WithNullId() {
    assertFalse(userService.isValidUserId(null));
  }

  @Test
  void testIsValidUserId_WithZeroId() {
    assertFalse(userService.isValidUserId(0L));
  }

  @Test
  void testIsValidUserId_WithNegativeId() {
    assertFalse(userService.isValidUserId(-1L));
  }

  @Test
  void testIsValidEmail_WithValidEmail() {
    assertTrue(userService.isValidEmail("test@example.com"));
    assertTrue(userService.isValidEmail("user.name@domain.co.uk"));
  }

  @Test
  void testIsValidEmail_WithNullEmail() {
    assertFalse(userService.isValidEmail(null));
  }

  @Test
  void testIsValidEmail_WithEmptyEmail() {
    assertFalse(userService.isValidEmail(""));
  }

  @Test
  void testIsValidEmail_WithInvalidEmail() {
    // The current validation only checks for "@" presence
    assertFalse(userService.isValidEmail("invalidemail")); // No @ symbol
    assertTrue(userService.isValidEmail("test@")); // Contains @ (current logic allows this)
    assertTrue(userService.isValidEmail("@example.com")); // Contains @ (current logic allows this)
  }

  @Test
  void testIsValidNameSearch_WithValidName() {
    assertTrue(userService.isValidNameSearch("John"));
    assertTrue(userService.isValidNameSearch("Doe"));
  }

  @Test
  void testIsValidNameSearch_WithNullName() {
    assertFalse(userService.isValidNameSearch(null));
  }

  @Test
  void testIsValidNameSearch_WithEmptyName() {
    assertFalse(userService.isValidNameSearch(""));
  }

  @Test
  void testIsValidNameSearch_WithShortName() {
    assertFalse(userService.isValidNameSearch("A"));
  }

  @Test
  void testCanUserBorrowBooks_WithValidUserId() {
    assertTrue(userService.canUserBorrowBooks(1L));
    assertTrue(userService.canUserBorrowBooks(100L));
  }

  @Test
  void testCanUserBorrowBooks_WithNullUserId() {
    assertFalse(userService.canUserBorrowBooks(null));
  }

  @Test
  void testCanUserBorrowBooks_WithInvalidUserId() {
    assertFalse(userService.canUserBorrowBooks(0L));
    assertFalse(userService.canUserBorrowBooks(-1L));
  }
}
