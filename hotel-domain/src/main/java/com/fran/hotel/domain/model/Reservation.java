package com.fran.hotel.domain.model;

import java.time.LocalDate;

public record Reservation(String id, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate, Rate rate, ReservationStatus status) {
}
