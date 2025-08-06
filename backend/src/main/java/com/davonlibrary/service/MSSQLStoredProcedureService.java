package com.davonlibrary.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for executing MSSQL stored procedures. Provides integration with the database stored
 * procedures for common operations.
 */
@ApplicationScoped
public class MSSQLStoredProcedureService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MSSQLStoredProcedureService.class);

  @Inject DataSource dataSource;

  @Inject EntityManager entityManager;

  /**
   * Execute the sp_borrow_book stored procedure.
   *
   * @param userId the user ID
   * @param bookCopyId the book copy ID
   * @param loanPeriodDays the loan period in days (default 14)
   * @return result message from the stored procedure
   */
  @Transactional
  public String borrowBook(Long userId, Long bookCopyId, Integer loanPeriodDays) {
    String sql = "{call sp_borrow_book(?, ?, ?)}";

    try (Connection connection = dataSource.getConnection();
        CallableStatement stmt = connection.prepareCall(sql)) {

      stmt.setLong(1, userId);
      stmt.setLong(2, bookCopyId);
      stmt.setInt(3, loanPeriodDays != null ? loanPeriodDays : 14);

      boolean hasResults = stmt.execute();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          if (rs.next()) {
            String message = rs.getString("message");
            LOGGER.info(
                "Book borrowed successfully: User={}, BookCopy={}, Message={}",
                userId,
                bookCopyId,
                message);
            return message;
          }
        }
      }

      LOGGER.info("Book borrowed successfully: User={}, BookCopy={}", userId, bookCopyId);
      return "Book borrowed successfully";

    } catch (SQLException e) {
      LOGGER.error("Error executing sp_borrow_book: User={}, BookCopy={}", userId, bookCopyId, e);
      throw new RuntimeException("Failed to borrow book: " + e.getMessage(), e);
    }
  }

  /**
   * Execute the sp_return_book stored procedure.
   *
   * @param loanId the loan ID
   * @return result message from the stored procedure
   */
  @Transactional
  public String returnBook(Long loanId) {
    String sql = "{call sp_return_book(?)}";

    try (Connection connection = dataSource.getConnection();
        CallableStatement stmt = connection.prepareCall(sql)) {

      stmt.setLong(1, loanId);

      boolean hasResults = stmt.execute();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          if (rs.next()) {
            String message = rs.getString("message");
            LOGGER.info("Book returned successfully: Loan={}, Message={}", loanId, message);
            return message;
          }
        }
      }

      LOGGER.info("Book returned successfully: Loan={}", loanId);
      return "Book returned successfully";

    } catch (SQLException e) {
      LOGGER.error("Error executing sp_return_book: Loan={}", loanId, e);
      throw new RuntimeException("Failed to return book: " + e.getMessage(), e);
    }
  }

  /**
   * Execute the sp_search_books stored procedure.
   *
   * @param searchTerm the search term
   * @param authorName the author name filter
   * @param isbn the ISBN filter
   * @return list of matching books
   */
  @Transactional
  public List<Map<String, Object>> searchBooks(String searchTerm, String authorName, String isbn) {
    String sql = "{call sp_search_books(?, ?, ?)}";
    List<Map<String, Object>> results = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        CallableStatement stmt = connection.prepareCall(sql)) {

      stmt.setString(1, searchTerm);
      stmt.setString(2, authorName);
      stmt.setString(3, isbn);

      boolean hasResults = stmt.execute();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          while (rs.next()) {
            Map<String, Object> book = new HashMap<>();
            book.put("id", rs.getLong("id"));
            book.put("title", rs.getString("title"));
            book.put("isbn", rs.getString("isbn"));
            book.put("availableCopies", rs.getInt("available_copies"));
            book.put("totalCopies", rs.getInt("total_copies"));
            book.put("authorName", rs.getString("author_name"));
            results.add(book);
          }
        }
      }

      LOGGER.info("Book search completed: Term={}, Results={}", searchTerm, results.size());
      return results;

    } catch (SQLException e) {
      LOGGER.error("Error executing sp_search_books: Term={}", searchTerm, e);
      throw new RuntimeException("Failed to search books: " + e.getMessage(), e);
    }
  }

  /**
   * Get overdue loans using a custom stored procedure.
   *
   * @return list of overdue loans
   */
  @Transactional
  public List<Map<String, Object>> getOverdueLoans() {
    String sql = "{call sp_get_overdue_loans}";
    List<Map<String, Object>> results = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        CallableStatement stmt = connection.prepareCall(sql)) {

      boolean hasResults = stmt.execute();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          while (rs.next()) {
            Map<String, Object> loan = new HashMap<>();
            loan.put("loanId", rs.getLong("loan_id"));
            loan.put("userId", rs.getLong("user_id"));
            loan.put("userName", rs.getString("user_name"));
            loan.put("bookTitle", rs.getString("book_title"));
            loan.put("dueDate", rs.getDate("due_date"));
            loan.put("daysOverdue", rs.getInt("days_overdue"));
            results.add(loan);
          }
        }
      }

      LOGGER.info("Retrieved {} overdue loans", results.size());
      return results;

    } catch (SQLException e) {
      LOGGER.error("Error executing sp_get_overdue_loans", e);
      throw new RuntimeException("Failed to get overdue loans: " + e.getMessage(), e);
    }
  }

  /**
   * Get library statistics using a custom stored procedure.
   *
   * @param libraryId the library ID (optional, null for all libraries)
   * @return library statistics
   */
  @Transactional
  public Map<String, Object> getLibraryStatistics(Long libraryId) {
    String sql = "{call sp_get_library_statistics(?)}";
    Map<String, Object> stats = new HashMap<>();

    try (Connection connection = dataSource.getConnection();
        CallableStatement stmt = connection.prepareCall(sql)) {

      if (libraryId != null) {
        stmt.setLong(1, libraryId);
      } else {
        stmt.setNull(1, Types.BIGINT);
      }

      boolean hasResults = stmt.execute();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          if (rs.next()) {
            stats.put("totalBooks", rs.getLong("total_books"));
            stats.put("totalCopies", rs.getLong("total_copies"));
            stats.put("availableCopies", rs.getLong("available_copies"));
            stats.put("totalLoans", rs.getLong("total_loans"));
            stats.put("activeLoans", rs.getLong("active_loans"));
            stats.put("overdueLoans", rs.getLong("overdue_loans"));
            stats.put("totalUsers", rs.getLong("total_users"));
            stats.put("totalFines", rs.getDouble("total_fines"));
            stats.put("outstandingFines", rs.getDouble("outstanding_fines"));
          }
        }
      }

      LOGGER.info("Retrieved library statistics: Library={}, Stats={}", libraryId, stats);
      return stats;

    } catch (SQLException e) {
      LOGGER.error("Error executing sp_get_library_statistics: Library={}", libraryId, e);
      throw new RuntimeException("Failed to get library statistics: " + e.getMessage(), e);
    }
  }

  /**
   * Execute a custom stored procedure with parameters.
   *
   * @param procedureName the name of the stored procedure
   * @param parameters the parameters to pass to the procedure
   * @return list of result maps
   */
  @Transactional
  public List<Map<String, Object>> executeCustomProcedure(
      String procedureName, Object... parameters) {
    StringBuilder sql = new StringBuilder("{call ").append(procedureName).append("(");
    for (int i = 0; i < parameters.length; i++) {
      if (i > 0) sql.append(", ");
      sql.append("?");
    }
    sql.append(")}");

    List<Map<String, Object>> results = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        CallableStatement stmt = connection.prepareCall(sql.toString())) {

      // Set parameters
      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i] == null) {
          stmt.setNull(i + 1, Types.VARCHAR);
        } else {
          stmt.setObject(i + 1, parameters[i]);
        }
      }

      boolean hasResults = stmt.execute();

      if (hasResults) {
        try (ResultSet rs = stmt.getResultSet()) {
          while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
              String columnName = rs.getMetaData().getColumnName(i);
              Object value = rs.getObject(i);
              row.put(columnName, value);
            }
            results.add(row);
          }
        }
      }

      LOGGER.info(
          "Executed custom procedure: {} with {} parameters, returned {} results",
          procedureName,
          parameters.length,
          results.size());
      return results;

    } catch (SQLException e) {
      LOGGER.error("Error executing custom procedure: {}", procedureName, e);
      throw new RuntimeException(
          "Failed to execute procedure " + procedureName + ": " + e.getMessage(), e);
    }
  }

  /**
   * Test the database connection and stored procedure access.
   *
   * @return connection test result
   */
  public Map<String, Object> testConnection() {
    Map<String, Object> result = new HashMap<>();

    try (Connection connection = dataSource.getConnection()) {
      result.put("status", "SUCCESS");
      result.put("database", connection.getMetaData().getDatabaseProductName());
      result.put("version", connection.getMetaData().getDatabaseProductVersion());
      result.put("driver", connection.getMetaData().getDriverName());
      result.put("url", connection.getMetaData().getURL());

      // Test if we can call a simple stored procedure
      try (CallableStatement stmt = connection.prepareCall("{call sp_get_library_statistics(?)}")) {
        stmt.setNull(1, Types.BIGINT);
        stmt.execute();
        result.put("storedProcedures", "ACCESSIBLE");
      } catch (SQLException e) {
        result.put("storedProcedures", "ERROR: " + e.getMessage());
      }

      LOGGER.info("Database connection test successful: {}", result);

    } catch (SQLException e) {
      result.put("status", "ERROR");
      result.put("message", e.getMessage());
      LOGGER.error("Database connection test failed", e);
    }

    return result;
  }
}
