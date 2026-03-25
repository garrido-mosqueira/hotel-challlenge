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

    public RoomTypeInventory increaseInventory() {
        return new RoomTypeInventory(
            this.id,
            this.hotelId,
            this.roomTypeId,
            this.date,
            this.totalInventory + 1,
            this.totalReserved
        );
    }

    public static RoomTypeInventory createNew(String hotelId, RoomType roomTypeId, LocalDate date) {
        return new RoomTypeInventory(
            null,
            hotelId,
            roomTypeId,
            date,
            1, // New room, so inventory is 1
            0
        );
    }
}
