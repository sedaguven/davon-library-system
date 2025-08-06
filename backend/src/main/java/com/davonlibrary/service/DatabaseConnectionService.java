package com.davonlibrary.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing database connections and providing database metadata. This service handles
 * MSSQL-specific operations and connection management.
 */
@ApplicationScoped
public class DatabaseConnectionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionService.class);

  @Inject DataSource dataSource;

  @ConfigProperty(name = "quarkus.datasource.db-kind")
  String databaseKind;

  @ConfigProperty(name = "quarkus.datasource.jdbc.url")
  String jdbcUrl;

  /**
   * Tests the database connection and returns connection status.
   *
   * @return true if connection is successful, false otherwise
   */
  public boolean testConnection() {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();

      LOGGER.info("Database connection successful");
      LOGGER.info(
          "Database: {} {}",
          metaData.getDatabaseProductName(),
          metaData.getDatabaseProductVersion());
      LOGGER.info("Driver: {} {}", metaData.getDriverName(), metaData.getDriverVersion());
      LOGGER.info("URL: {}", jdbcUrl);

      return true;
    } catch (SQLException e) {
      LOGGER.error("Database connection failed: {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * Gets database metadata information.
   *
   * @return DatabaseMetadata object containing database information
   * @throws SQLException if database access error occurs
   */
  public DatabaseMetadata getDatabaseMetadata() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();

      return new DatabaseMetadata(
          metaData.getDatabaseProductName(),
          metaData.getDatabaseProductVersion(),
          metaData.getDriverName(),
          metaData.getDriverVersion(),
          jdbcUrl,
          databaseKind);
    }
  }

  /**
   * Checks if the current database is MSSQL.
   *
   * @return true if using MSSQL, false otherwise
   */
  public boolean isMSSQL() {
    return "mssql".equalsIgnoreCase(databaseKind);
  }

  /**
   * Gets the current database kind.
   *
   * @return the database kind (h2, mssql, etc.)
   */
  public String getDatabaseKind() {
    return databaseKind;
  }

  /**
   * Gets the JDBC URL.
   *
   * @return the JDBC connection URL
   */
  public String getJdbcUrl() {
    return jdbcUrl;
  }

  /** Inner class to hold database metadata information. */
  public static class DatabaseMetadata {
    private final String productName;
    private final String productVersion;
    private final String driverName;
    private final String driverVersion;
    private final String jdbcUrl;
    private final String databaseKind;

    public DatabaseMetadata(
        String productName,
        String productVersion,
        String driverName,
        String driverVersion,
        String jdbcUrl,
        String databaseKind) {
      this.productName = productName;
      this.productVersion = productVersion;
      this.driverName = driverName;
      this.driverVersion = driverVersion;
      this.jdbcUrl = jdbcUrl;
      this.databaseKind = databaseKind;
    }

    // Getters
    public String getProductName() {
      return productName;
    }

    public String getProductVersion() {
      return productVersion;
    }

    public String getDriverName() {
      return driverName;
    }

    public String getDriverVersion() {
      return driverVersion;
    }

    public String getJdbcUrl() {
      return jdbcUrl;
    }

    public String getDatabaseKind() {
      return databaseKind;
    }

    @Override
    public String toString() {
      return String.format(
          "DatabaseMetadata{productName='%s', productVersion='%s', "
              + "driverName='%s', driverVersion='%s', databaseKind='%s'}",
          productName, productVersion, driverName, driverVersion, databaseKind);
    }
  }
}
