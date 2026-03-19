package com.fran.hotel.domain.model;

public record Room(
    String roomId,
    Long hotelId,
    String roomTypeId,
    int floor,
    String number,
    String name,
    boolean isAvailable
) {}
