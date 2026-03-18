package com.fran.hotel.domain.model;

import java.time.LocalDate;

public record Reservation(
    Long reservationId,
    Long hotelId,
    String roomTypeId,
    Long guestId,
    LocalDate startDate,
    LocalDate endDate,
    ReservationStatus status
) {}
