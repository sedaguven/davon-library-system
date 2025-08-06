package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.Author;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AuthorServiceTest {

  @Inject AuthorService authorService;

  @Test
  void testServiceInjection() {
    assertNotNull(authorService);
  }

  @Test
  void testIsValidAuthor_WithValidAuthor() {
    Author author = new Author();
    author.firstName = "John";
    author.lastName = "Doe";

    assertTrue(authorService.isValidAuthor(author));
  }

  @Test
  void testIsValidAuthor_WithNullAuthor() {
    assertFalse(authorService.isValidAuthor(null));
  }

  @Test
  void testIsValidAuthor_WithNullFirstName() {
    Author author = new Author();
    author.firstName = null;
    author.lastName = "Doe";

    assertFalse(authorService.isValidAuthor(author));
  }

  @Test
  void testIsValidAuthor_WithEmptyFirstName() {
    Author author = new Author();
    author.firstName = "";
    author.lastName = "Doe";

    assertFalse(authorService.isValidAuthor(author));
  }

  @Test
  void testIsValidAuthor_WithNullLastName() {
    Author author = new Author();
    author.firstName = "John";
    author.lastName = null;

    assertFalse(authorService.isValidAuthor(author));
  }

  @Test
  void testIsValidAuthor_WithEmptyLastName() {
    Author author = new Author();
    author.firstName = "John";
    author.lastName = "";

    assertFalse(authorService.isValidAuthor(author));
  }

  @Test
  void testIsValidAuthorId_WithValidId() {
    assertTrue(authorService.isValidAuthorId(1L));
    assertTrue(authorService.isValidAuthorId(100L));
  }

  @Test
  void testIsValidAuthorId_WithNullId() {
    assertFalse(authorService.isValidAuthorId(null));
  }

  @Test
  void testIsValidAuthorId_WithZeroId() {
    assertFalse(authorService.isValidAuthorId(0L));
  }

  @Test
  void testIsValidAuthorId_WithNegativeId() {
    assertFalse(authorService.isValidAuthorId(-1L));
  }

  @Test
  void testIsValidNameSearch_WithValidName() {
    assertTrue(authorService.isValidNameSearch("John"));
    assertTrue(authorService.isValidNameSearch("Doe"));
  }

  @Test
  void testIsValidNameSearch_WithNullName() {
    assertFalse(authorService.isValidNameSearch(null));
  }

  @Test
  void testIsValidNameSearch_WithEmptyName() {
    assertFalse(authorService.isValidNameSearch(""));
  }

  @Test
  void testIsValidNameSearch_WithShortName() {
    assertFalse(authorService.isValidNameSearch("A"));
  }

  @Test
  void testIsValidAuthorNames_WithValidNames() {
    assertTrue(authorService.isValidAuthorNames("John", "Doe"));
    assertTrue(authorService.isValidAuthorNames("Jane", "Smith"));
  }

  @Test
  void testIsValidAuthorNames_WithNullFirstName() {
    assertFalse(authorService.isValidAuthorNames(null, "Doe"));
  }

  @Test
  void testIsValidAuthorNames_WithEmptyFirstName() {
    assertFalse(authorService.isValidAuthorNames("", "Doe"));
  }

  @Test
  void testIsValidAuthorNames_WithNullLastName() {
    assertFalse(authorService.isValidAuthorNames("John", null));
  }

  @Test
  void testIsValidAuthorNames_WithEmptyLastName() {
    assertFalse(authorService.isValidAuthorNames("John", ""));
  }
}
