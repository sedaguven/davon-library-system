package com.davonlibrary.resource;

import com.davonlibrary.dto.ReservationDTO;
import com.davonlibrary.entity.Reservation;
import com.davonlibrary.service.ReservationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

  @Inject private ReservationService reservationService;

  @GET
  @Path("/user/{userId}/with-queue-position")
  public Response getReservationsWithQueuePosition(@PathParam("userId") Long userId) {
    try {
      List<ReservationDTO> reservations =
          reservationService.getReservationsWithQueuePosition(userId);
      return Response.ok(reservations).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @POST
  public Response createReservation(Reservation reservation) {
    try {
      Reservation createdReservation = reservationService.createReservation(reservation);
      return Response.status(Response.Status.CREATED).entity(createdReservation).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("/user/{userId}")
  public Response getReservationsByUserId(@PathParam("userId") Long userId) {
    if (!reservationService.isValidUserId(userId)) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid user ID.").build();
    }
    List<ReservationDTO> reservations = reservationService.getReservationsWithQueuePosition(userId);
    return Response.ok(reservations).build();
  }

  @PUT
  @Path("/{reservationId}/cancel")
  public Response cancelReservation(@PathParam("reservationId") Long reservationId) {
    try {
      Reservation cancelledReservation = reservationService.cancelReservation(reservationId);
      return Response.ok(cancelledReservation).build();
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    } catch (WebApplicationException e) {
      return e.getResponse();
    }
  }

  @GET
  @Path("/queue-position")
  public Response getQueuePosition(
      @QueryParam("userId") Long userId, @QueryParam("bookId") Long bookId) {
    try {
      Optional<Integer> queuePosition = reservationService.getQueuePosition(userId, bookId);
      if (queuePosition.isPresent()) {
        return Response.ok(queuePosition.get()).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).entity("Reservation not found.").build();
      }
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }
}
