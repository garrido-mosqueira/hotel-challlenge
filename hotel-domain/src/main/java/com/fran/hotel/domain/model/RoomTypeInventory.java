package com.fran.hotel.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record RoomTypeInventory(
    String id,
    String hotelId,
    RoomType roomTypeId,
    LocalDate date,
    int totalInventory,
    int totalReserved
) {
    public boolean hasAvailability() {
        return totalReserved < totalInventory;
    }
}
