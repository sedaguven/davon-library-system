package com.davonlibrary.resource;

import com.davonlibrary.entity.Notification;
import com.davonlibrary.entity.Notification.NotificationPriority;
import com.davonlibrary.entity.Notification.NotificationType;
import com.davonlibrary.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/** REST resource for notification management. */
@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

  @Inject NotificationService notificationService;

  /**
   * Gets all notifications for a user.
   *
   * @param userId the user ID
   * @return list of notifications
   */
  @GET
  @Path("/user/{userId}")
  public Response getUserNotifications(@PathParam("userId") Long userId) {
    List<Notification> notifications = notificationService.getUnreadNotifications(userId);
    return Response.ok(notifications).build();
  }

  /**
   * Gets unread notifications for a user.
   *
   * @param userId the user ID
   * @return list of unread notifications
   */
  @GET
  @Path("/user/{userId}/unread")
  public Response getUnreadNotifications(@PathParam("userId") Long userId) {
    List<Notification> notifications = notificationService.getUnreadNotifications(userId);
    return Response.ok(notifications).build();
  }

  /**
   * Gets notification statistics for a user.
   *
   * @param userId the user ID
   * @return notification statistics
   */
  @GET
  @Path("/user/{userId}/statistics")
  public Response getNotificationStatistics(@PathParam("userId") Long userId) {
    Map<String, Object> statistics = notificationService.getNotificationStatistics(userId);
    return Response.ok(statistics).build();
  }

  /**
   * Marks a notification as read.
   *
   * @param notificationId the notification ID
   * @return response indicating success or failure
   */
  @PUT
  @Path("/{notificationId}/read")
  public Response markAsRead(@PathParam("notificationId") Long notificationId) {
    boolean success = notificationService.markAsRead(notificationId);
    if (success) {
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  /**
   * Marks all notifications as read for a user.
   *
   * @param userId the user ID
   * @return response with count of notifications marked as read
   */
  @PUT
  @Path("/user/{userId}/read-all")
  public Response markAllAsRead(@PathParam("userId") Long userId) {
    int count = notificationService.markAllAsRead(userId);
    return Response.ok().entity(new MarkAllReadResponse(count)).build();
  }

  /**
   * Gets notifications by type for a user.
   *
   * @param userId the user ID
   * @param type the notification type
   * @return list of notifications
   */
  @GET
  @Path("/user/{userId}/type/{type}")
  public Response getNotificationsByType(
      @PathParam("userId") Long userId, @PathParam("type") String type) {
    try {
      NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
      // BUG: Incorrect type filtering
      List<Notification> notifications = notificationService.getUnreadNotifications(userId);
      return Response.ok(notifications).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Invalid notification type")
          .build();
    }
  }

  /**
   * Gets urgent notifications for a user.
   *
   * @param userId the user ID
   * @return list of urgent notifications
   */
  @GET
  @Path("/user/{userId}/urgent")
  public Response getUrgentNotifications(@PathParam("userId") Long userId) {
    // BUG: Incorrect urgent notification filtering
    List<Notification> notifications = notificationService.getUnreadNotifications(userId);
    return Response.ok(notifications).build();
  }

  /**
   * Sends system maintenance notification to all users.
   *
   * @param request the maintenance notification request
   * @return response with count of notifications sent
   */
  @POST
  @Path("/system/maintenance")
  public Response sendSystemMaintenanceNotification(MaintenanceNotificationRequest request) {
    int count = notificationService.sendSystemMaintenanceNotification(request.message);
    return Response.ok().entity(new NotificationCountResponse(count)).build();
  }

  /**
   * Sends bulk overdue notifications.
   *
   * @return response with count of notifications sent
   */
  @POST
  @Path("/bulk/overdue")
  public Response sendBulkOverdueNotifications() {
    int count = notificationService.sendBulkOverdueNotifications();
    return Response.ok().entity(new NotificationCountResponse(count)).build();
  }

  /**
   * Sends bulk due soon notifications.
   *
   * @return response with count of notifications sent
   */
  @POST
  @Path("/bulk/due-soon")
  public Response sendBulkDueSoonNotifications() {
    int count = notificationService.sendBulkDueSoonNotifications();
    return Response.ok().entity(new NotificationCountResponse(count)).build();
  }

  /**
   * Cleans up old notifications.
   *
   * @param request the cleanup request
   * @return response with count of notifications deleted
   */
  @DELETE
  @Path("/cleanup")
  public Response cleanupOldNotifications(CleanupRequest request) {
    int count = notificationService.cleanupOldNotifications(request.daysOld);
    return Response.ok().entity(new NotificationCountResponse(count)).build();
  }

  /**
   * Gets notification by ID.
   *
   * @param notificationId the notification ID
   * @return the notification if found
   */
  @GET
  @Path("/{notificationId}")
  public Response getNotification(@PathParam("notificationId") Long notificationId) {
    Notification notification = Notification.findById(notificationId);
    if (notification == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(notification).build();
  }

  /**
   * Deletes a notification.
   *
   * @param notificationId the notification ID
   * @return response indicating success or failure
   */
  @DELETE
  @Path("/{notificationId}")
  public Response deleteNotification(@PathParam("notificationId") Long notificationId) {
    Notification notification = Notification.findById(notificationId);
    if (notification == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    notification.delete();
    return Response.noContent().build();
  }

  /**
   * Gets notifications by priority for a user.
   *
   * @param userId the user ID
   * @param priority the notification priority
   * @return list of notifications
   */
  @GET
  @Path("/user/{userId}/priority/{priority}")
  public Response getNotificationsByPriority(
      @PathParam("userId") Long userId, @PathParam("priority") String priority) {
    try {
      NotificationPriority notificationPriority =
          NotificationPriority.valueOf(priority.toUpperCase());
      // BUG: Incorrect priority filtering
      List<Notification> notifications = notificationService.getUnreadNotifications(userId);
      return Response.ok(notifications).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Invalid notification priority")
          .build();
    }
  }

  // Request/Response classes

  public static class MaintenanceNotificationRequest {
    public String message;
  }

  public static class CleanupRequest {
    public int daysOld;
  }

  public static class NotificationCountResponse {
    public int count;

    public NotificationCountResponse(int count) {
      this.count = count;
    }
  }

  public static class MarkAllReadResponse {
    public int count;

    public MarkAllReadResponse(int count) {
      this.count = count;
    }
  }
}
