package com.davonlibrary.service;

import static org.junit.jupiter.api.Assertions.*;

import com.davonlibrary.entity.Book;
import com.davonlibrary.entity.Reservation;
import com.davonlibrary.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReservationServiceTest {

  @Inject ReservationService reservationService;

  @Test
  void testServiceInjection() {
    assertNotNull(reservationService);
  }

  @Test
  void testIsValidReservation_WithValidReservation() {
    Reservation reservation = new Reservation();
    reservation.user = new User();
    reservation.book = new Book();

    assertTrue(reservationService.isValidReservation(reservation));
  }

  @Test
  void testIsValidReservation_WithNullReservation() {
    assertFalse(reservationService.isValidReservation(null));
  }

  @Test
  void testIsValidReservation_WithNullUser() {
    Reservation reservation = new Reservation();
    reservation.user = null;
    reservation.book = new Book();

    assertFalse(reservationService.isValidReservation(reservation));
  }

  @Test
  void testIsValidReservation_WithNullBook() {
    Reservation reservation = new Reservation();
    reservation.user = new User();
    reservation.book = null;

    assertFalse(reservationService.isValidReservation(reservation));
  }

  @Test
  void testIsValidReservationId_WithValidId() {
    assertTrue(reservationService.isValidReservationId(1L));
    assertTrue(reservationService.isValidReservationId(100L));
  }

  @Test
  void testIsValidReservationId_WithNullId() {
    assertFalse(reservationService.isValidReservationId(null));
  }

  @Test
  void testIsValidReservationId_WithZeroId() {
    assertFalse(reservationService.isValidReservationId(0L));
  }

  @Test
  void testIsValidReservationId_WithNegativeId() {
    assertFalse(reservationService.isValidReservationId(-1L));
  }

  @Test
  void testIsValidUserId_WithValidId() {
    assertTrue(reservationService.isValidUserId(1L));
    assertTrue(reservationService.isValidUserId(100L));
  }

  @Test
  void testIsValidUserId_WithNullId() {
    assertFalse(reservationService.isValidUserId(null));
  }

  @Test
  void testIsValidUserId_WithZeroId() {
    assertFalse(reservationService.isValidUserId(0L));
  }

  @Test
  void testIsValidUserId_WithNegativeId() {
    assertFalse(reservationService.isValidUserId(-1L));
  }

  @Test
  void testIsValidBookId_WithValidId() {
    assertTrue(reservationService.isValidBookId(1L));
    assertTrue(reservationService.isValidBookId(100L));
  }

  @Test
  void testIsValidBookId_WithNullId() {
    assertFalse(reservationService.isValidBookId(null));
  }

  @Test
  void testIsValidBookId_WithZeroId() {
    assertFalse(reservationService.isValidBookId(0L));
  }

  @Test
  void testIsValidBookId_WithNegativeId() {
    assertFalse(reservationService.isValidBookId(-1L));
  }
}
