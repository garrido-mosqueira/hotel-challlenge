package com.fran.hotel.domain.model;

import com.fran.hotel.domain.exception.InvalidReservationStateException;
import com.fran.hotel.domain.exception.ReservationAlreadyCancelledException;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record Reservation(
        String id,
        String guestId,
        String roomId,
        String roomName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        ReservationStatus status) {

    public Reservation confirm() {
        return switch (this.status) {
            case CANCELLED, REFUNDED -> throw new InvalidReservationStateException("Cannot confirm a cancelled or refunded reservation");
            default -> new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.CONFIRMED);
        };
    }

    public Reservation cancel() {
        return switch (this.status) {
            case CONFIRMED -> new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.REFUNDED);
            case REFUNDED -> this;
            case CANCELLED -> throw new ReservationAlreadyCancelledException("Reservation is already cancelled");
            case PENDING -> new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.CANCELLED);
        };
    }

    public Reservation pending() {
        return new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.PENDING);
    }

}
