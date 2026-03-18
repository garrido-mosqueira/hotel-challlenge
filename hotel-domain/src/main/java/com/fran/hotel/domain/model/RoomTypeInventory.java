package com.fran.hotel.domain.model;

import java.time.LocalDate;

public record RoomTypeInventory(
    Long hotelId,
    String roomTypeId,
    LocalDate date,
    int totalInventory,
    int totalReserved
) {
    public boolean hasAvailability() {
        return totalReserved < totalInventory;
    }
}
