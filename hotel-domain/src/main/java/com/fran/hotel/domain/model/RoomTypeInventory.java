package com.fran.hotel.domain.model;

import java.time.LocalDate;

public record RoomTypeInventory(
    String id,
    String hotelId,
    String roomTypeId,
    LocalDate date,
    int totalInventory,
    int totalReserved
) {
    public boolean hasAvailability() {
        return totalReserved < totalInventory;
    }
}
