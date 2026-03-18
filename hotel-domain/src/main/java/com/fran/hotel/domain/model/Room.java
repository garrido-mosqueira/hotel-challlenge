package com.fran.hotel.domain.model;

public record Room(
    Long roomId,
    Long hotelId,
    String roomTypeId,
    int floor,
    String number,
    String name,
    boolean isAvailable
) {}
