package com.davonlibrary.resource;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserResourceTest {

  @Test
  void testCreateUserRequest_WithValidData() {
    UserResource.CreateUserRequest request = new UserResource.CreateUserRequest();
    request.firstName = "John";
    request.lastName = "Doe";
    request.email = "john.doe@example.com";
    request.libraryId = 1L;

    assertNotNull(request);
    assertEquals("John", request.firstName);
    assertEquals("Doe", request.lastName);
    assertEquals("john.doe@example.com", request.email);
    assertEquals(1L, request.libraryId);
  }

  @Test
  void testCreateUserRequest_WithNullValues() {
    UserResource.CreateUserRequest request = new UserResource.CreateUserRequest();

    assertNotNull(request);
    assertNull(request.firstName);
    assertNull(request.lastName);
    assertNull(request.email);
    assertNull(request.libraryId);
  }

  @Test
  void testUpdateUserRequest_WithValidData() {
    UserResource.UpdateUserRequest request = new UserResource.UpdateUserRequest();
    request.firstName = "Jane";
    request.lastName = "Smith";
    request.email = "jane.smith@example.com";

    assertNotNull(request);
    assertEquals("Jane", request.firstName);
    assertEquals("Smith", request.lastName);
    assertEquals("jane.smith@example.com", request.email);
  }

  @Test
  void testUpdateUserRequest_WithNullValues() {
    UserResource.UpdateUserRequest request = new UserResource.UpdateUserRequest();

    assertNotNull(request);
    assertNull(request.firstName);
    assertNull(request.lastName);
    assertNull(request.email);
  }

  @Test
  void testResourceClassExists() {
    assertNotNull(UserResource.class);
  }

  @Test
  void testResourceAnnotations() {
    assertTrue(UserResource.class.isAnnotationPresent(jakarta.ws.rs.Path.class));
    assertTrue(UserResource.class.isAnnotationPresent(jakarta.ws.rs.Produces.class));
    assertTrue(UserResource.class.isAnnotationPresent(jakarta.ws.rs.Consumes.class));
  }
}
