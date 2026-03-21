package com.fran.hotel.domain.model;

public record Room(
    String id,
    String hotelId,
    String roomTypeId,
    int floor,
    String number,
    String name,
    boolean isAvailable
) {}
