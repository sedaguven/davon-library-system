package com.davonlibrary.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class BookResourceTest {

  @Test
  public void testGetAllBooksEndpoint() {
    given().when().get("/api/books").then().statusCode(200).body("$", hasSize(31));
  }

  @Test
  public void testGetBookByIdEndpoint_Found() {
    given()
        .pathParam("id", 1)
        .when()
        .get("/api/books/{id}")
        .then()
        .statusCode(200)
        .body("title", is("Harry Potter and the Philosopher's Stone"));
  }

  @Test
  public void testGetBookByIdEndpoint_NotFound() {
    given().pathParam("id", 999).when().get("/api/books/{id}").then().statusCode(404);
  }
}
