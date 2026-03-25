package com.fran.hotel.application.service;

import com.fran.hotel.domain.exception.InvalidReservationStateException;
import com.fran.hotel.domain.exception.ReservationAlreadyCancelledException;
import com.fran.hotel.domain.exception.ReservationAvailabilityException;
import com.fran.hotel.domain.exception.ReservationNotFoundException;
import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.ReservationPaymentPort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationPersistencePort persistence;
    @Mock
    private ReservationPaymentPort paymentPort;
    @Mock
    private RoomPersistencePort roomPersistencePort;
    @Mock
    private RoomTypeInventoryPersistencePort roomTypeInventoryPersistencePort;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservationShouldFailWhenNoAvailability() {
        // Given
        String roomId = "room-1";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);
        Reservation reservation = new Reservation(null, "guest-1", roomId, "Room 101", checkIn, checkOut, ReservationStatus.PENDING);

        Room room = new Room(roomId, "hotel-1", "type-1", 1, "101", "Room 101", true);
        when(roomPersistencePort.findById(roomId)).thenReturn(room);

        RoomTypeInventory inventory1 = new RoomTypeInventory("inv-1", "hotel-1", "type-1", checkIn, 1, 1);
        RoomTypeInventory inventory2 = new RoomTypeInventory("inv-2", "hotel-1", "type-1", checkIn.plusDays(1), 1, 0);
        when(roomTypeInventoryPersistencePort.findByHotelIdAndRoomTypeIdAndDateBetween(
                eq("hotel-1"), eq("type-1"), eq(checkIn), eq(checkOut.minusDays(1))))
                .thenReturn(List.of(inventory1, inventory2));

        // When & Then
        ReservationAvailabilityException exception = assertThrows(ReservationAvailabilityException.class, () -> reservationService.createReservation(reservation));
        assertEquals("No availability for the selected dates", exception.getMessage());
        verify(persistence, never()).save(any());
    }

    @Test
    void createReservationShouldFailWhenOverlapsWithPendingReservation() {
        // Given
        String roomId = "room-1";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);
        Reservation reservation = new Reservation(null, "guest-1", roomId, "Room 101", checkIn, checkOut, ReservationStatus.PENDING);

        Room room = new Room(roomId, "hotel-1", "type-1", 1, "101", "Room 101", true);
        when(roomPersistencePort.findById(roomId)).thenReturn(room);

        // Assume inventory is available
        RoomTypeInventory inventory1 = new RoomTypeInventory("inv-1", "hotel-1", "type-1", checkIn, 1, 0);
        RoomTypeInventory inventory2 = new RoomTypeInventory("inv-2", "hotel-1", "type-1", checkIn.plusDays(1), 1, 0);
        when(roomTypeInventoryPersistencePort.findByHotelIdAndRoomTypeIdAndDateBetween(
                eq("hotel-1"), eq("type-1"), eq(checkIn), eq(checkOut.minusDays(1))))
                .thenReturn(List.of(inventory1, inventory2));

        // Mock overlapping reservation
        Reservation overlappingReservation = new Reservation("res-2", "guest-2", roomId, "Room 101", checkIn.plusDays(1), checkOut.plusDays(1), ReservationStatus.PENDING);
        when(persistence.findOverlappingReservations(roomId, checkIn, checkOut)).thenReturn(List.of(overlappingReservation));

        // When & Then
        ReservationAvailabilityException exception = assertThrows(ReservationAvailabilityException.class, () -> reservationService.createReservation(reservation));
        assertEquals("Room is already reserved for the selected dates", exception.getMessage());
        verify(persistence, never()).save(any());
    }

    @Test
    void createReservationShouldSucceedWhenAvailabilityExists() {
        // Given
        String roomId = "room-1";
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);
        Reservation reservation = new Reservation(null, "guest-1", roomId, "Room 101", checkIn, checkOut, ReservationStatus.PENDING);

        Room room = new Room(roomId, "hotel-1", "type-1", 1, "101", "Room 101", true);
        when(roomPersistencePort.findById(roomId)).thenReturn(room);

        RoomTypeInventory inventory1 = new RoomTypeInventory("inv-1", "hotel-1", "type-1", checkIn, 1, 0);
        RoomTypeInventory inventory2 = new RoomTypeInventory("inv-2", "hotel-1", "type-1", checkIn.plusDays(1), 1, 0);
        when(roomTypeInventoryPersistencePort.findByHotelIdAndRoomTypeIdAndDateBetween(
                eq("hotel-1"), eq("type-1"), eq(checkIn), eq(checkOut.minusDays(1))))
                .thenReturn(List.of(inventory1, inventory2));

        when(persistence.findOverlappingReservations(roomId, checkIn, checkOut)).thenReturn(Collections.emptyList());

        Reservation savedReservation = reservation.confirm();
        when(persistence.save(reservation)).thenReturn(savedReservation);
        when(paymentPort.executeReservationPayment(savedReservation)).thenReturn(savedReservation);

        // When
        Reservation result = reservationService.createReservation(reservation);

        // Then
        assertNotNull(result);
        verify(persistence).save(reservation);
        verify(paymentPort).executeReservationPayment(savedReservation);
    }

    @Test
    void confirmShouldFailWhenCancelled() {
        Reservation reservation = new Reservation("1", "guest-1", "room-1", "Room 1", LocalDate.now(), LocalDate.now().plusDays(1), ReservationStatus.CANCELLED);
        assertThrows(InvalidReservationStateException.class, reservation::confirm);
    }

    @Test
    void cancelShouldFailWhenAlreadyCancelled() {
        Reservation reservation = new Reservation("1", "guest-1", "room-1", "Room 1", LocalDate.now(), LocalDate.now().plusDays(1), ReservationStatus.CANCELLED);
        assertThrows(ReservationAlreadyCancelledException.class, reservation::cancel);
    }

    @Test
    void cancelReservationInServiceShouldFailWhenAlreadyCancelled() {
        String reservationId = "res-1";
        Reservation reservation = new Reservation(reservationId, "guest-1", "room-1", "Room 1", LocalDate.now(), LocalDate.now().plusDays(1), ReservationStatus.CANCELLED);
        when(persistence.findById(reservationId)).thenReturn(reservation);

        assertThrows(ReservationAlreadyCancelledException.class, () -> reservationService.cancelReservation(reservationId));
    }

    @Test
    void cancelReservationInServiceShouldFailWhenNotFound() {
        String reservationId = "non-existent";
        when(persistence.findById(reservationId)).thenReturn(null);

        assertThrows(ReservationNotFoundException.class, () -> reservationService.cancelReservation(reservationId));
    }
}
