package com.fran.hotel.domain.model;

import java.time.LocalDate;

public record Reservation(
        String id,
        String guestId,
        String roomId,
        String rateId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        ReservationStatus status) {

    public Reservation withStatus(ReservationStatus newStatus) {
        return new Reservation(id, guestId, roomId, rateId, checkInDate, checkOutDate, newStatus);
    }
}
