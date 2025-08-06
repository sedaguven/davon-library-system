package com.davonlibrary.resource;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LibraryResourceTest {

  @Test
  public void testBorrowOrReserveBook_Success() {
    // This test assumes user ID 1 and book ID 1 exist and that book 1 has available copies.
    // You may need to adjust these IDs based on the actual data in your database.
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"userId\": 1, \"bookId\": 1}")
        .when()
        .post("/api/library/borrow-or-reserve")
        .then()
        .statusCode(200);
  }

  @Test
  public void testBorrowOrReserveBook_BookNotFound() {
    given()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"userId\": 1, \"bookId\": 9999}") // Assuming book 9999 does not exist
        .when()
        .post("/api/library/borrow-or-reserve")
        .then()
        .statusCode(400); // Bad Request for not found entities
  }
}
