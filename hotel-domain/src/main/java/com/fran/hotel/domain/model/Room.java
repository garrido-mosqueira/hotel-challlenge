package com.fran.hotel.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record Room(
    String id,
    String hotelId,
    RoomType typeId,
    int floor,
    String number,
    String name,
    boolean isAvailable
) {
}
