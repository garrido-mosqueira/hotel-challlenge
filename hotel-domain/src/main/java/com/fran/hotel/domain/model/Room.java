package com.fran.hotel.domain.model;

public record Room(
    String id,
    String hotelId,
    String typeId,
    int floor,
    String number,
    String name,
    boolean isAvailable
) {
}
