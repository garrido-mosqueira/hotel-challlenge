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
        if (this.status == ReservationStatus.CANCELLED) {
            throw new InvalidReservationStateException("Cannot confirm a cancelled reservation");
        }
        return new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.CONFIRMED);
    }

    public Reservation cancel() {
        if (this.status == ReservationStatus.CONFIRMED) {
            // Depending on business rules, we might allow cancelling a confirmed one or not.
            // For now, let's just allow it but maybe prevent cancelling an already cancelled one.
        }
        if (this.status == ReservationStatus.CANCELLED) {
            throw new ReservationAlreadyCancelledException("Reservation is already cancelled");
        }
        return new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.CANCELLED);
    }

    public Reservation pending() {
        return new Reservation(id, guestId, roomId, roomName, checkInDate, checkOutDate, ReservationStatus.PENDING);
    }

}
