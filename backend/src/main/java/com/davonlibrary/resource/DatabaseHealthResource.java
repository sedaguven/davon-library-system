package com.davonlibrary.resource;

import com.davonlibrary.service.DatabaseConnectionService;
import com.davonlibrary.service.DatabaseConnectionService.DatabaseMetadata;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST resource for database health checks and connection status. Provides endpoints to verify
 * database connectivity and get database information.
 */
@Path("/api/database")
@Produces(MediaType.APPLICATION_JSON)
public class DatabaseHealthResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthResource.class);

  @Inject DatabaseConnectionService databaseConnectionService;

  /**
   * Health check endpoint to verify database connection.
   *
   * @return Response with connection status
   */
  @GET
  @Path("/health")
  public Response healthCheck() {
    Map<String, Object> response = new HashMap<>();

    try {
      boolean isConnected = databaseConnectionService.testConnection();
      String databaseKind = databaseConnectionService.getDatabaseKind();

      response.put("status", isConnected ? "UP" : "DOWN");
      response.put("database", databaseKind);
      response.put("timestamp", System.currentTimeMillis());

      if (isConnected) {
        response.put("message", "Database connection is healthy");
        return Response.ok(response).build();
      } else {
        response.put("message", "Database connection failed");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(response).build();
      }
    } catch (Exception e) {
      LOGGER.error("Health check failed", e);
      response.put("status", "DOWN");
      response.put("error", e.getMessage());
      response.put("timestamp", System.currentTimeMillis());

      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
  }

  /**
   * Get detailed database information and metadata.
   *
   * @return Response with database metadata
   */
  @GET
  @Path("/info")
  public Response getDatabaseInfo() {
    Map<String, Object> response = new HashMap<>();

    try {
      DatabaseMetadata metadata = databaseConnectionService.getDatabaseMetadata();

      response.put("databaseKind", metadata.getDatabaseKind());
      response.put("productName", metadata.getProductName());
      response.put("productVersion", metadata.getProductVersion());
      response.put("driverName", metadata.getDriverName());
      response.put("driverVersion", metadata.getDriverVersion());
      response.put("jdbcUrl", metadata.getJdbcUrl());
      response.put("isMSSQL", databaseConnectionService.isMSSQL());
      response.put("timestamp", System.currentTimeMillis());

      return Response.ok(response).build();
    } catch (SQLException e) {
      LOGGER.error("Failed to get database info", e);
      response.put("error", "Failed to retrieve database information: " + e.getMessage());
      response.put("timestamp", System.currentTimeMillis());

      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
  }

  /**
   * Test MSSQL-specific features and compatibility.
   *
   * @return Response with MSSQL compatibility status
   */
  @GET
  @Path("/mssql-test")
  public Response testMSSQLFeatures() {
    Map<String, Object> response = new HashMap<>();

    if (!databaseConnectionService.isMSSQL()) {
      response.put("error", "This endpoint is only available for MSSQL databases");
      response.put("currentDatabase", databaseConnectionService.getDatabaseKind());
      return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    try {
      boolean isConnected = databaseConnectionService.testConnection();
      DatabaseMetadata metadata = databaseConnectionService.getDatabaseMetadata();

      response.put("mssqlCompatible", true);
      response.put("connectionStatus", isConnected ? "CONNECTED" : "DISCONNECTED");
      response.put("productName", metadata.getProductName());
      response.put("productVersion", metadata.getProductVersion());
      response.put("message", "MSSQL database is properly configured and accessible");
      response.put("timestamp", System.currentTimeMillis());

      return Response.ok(response).build();
    } catch (Exception e) {
      LOGGER.error("MSSQL test failed", e);
      response.put("mssqlCompatible", false);
      response.put("error", e.getMessage());
      response.put("timestamp", System.currentTimeMillis());

      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
  }
}
