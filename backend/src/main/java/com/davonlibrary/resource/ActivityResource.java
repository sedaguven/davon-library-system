package com.davonlibrary.resource;

import com.davonlibrary.dto.ActivityDTO;
import com.davonlibrary.service.ActivityService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/activities")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityResource {

  @Inject ActivityService activityService;

  @GET
  @Path("/recent")
  public List<ActivityDTO> getRecentActivities(@QueryParam("limit") @DefaultValue("10") int limit) {
    return activityService.getRecentActivities(limit);
  }
}
