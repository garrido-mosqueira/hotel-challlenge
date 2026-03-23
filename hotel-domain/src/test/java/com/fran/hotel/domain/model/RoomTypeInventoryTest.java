package com.fran.hotel.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class RoomTypeInventoryTest {

    @Test
    void hasAvailabilityShouldReturnTrueWhenReservedIsLessThanInventory() {
        RoomTypeInventory inventory = new RoomTypeInventory(
                "inv-1",
                "hotel-1",
                "type-1",
                LocalDate.now(),
                10,
                5
        );

        assertTrue(inventory.hasAvailability());
    }

    @Test
    void hasAvailabilityShouldReturnFalseWhenReservedIsEqualToInventory() {
        RoomTypeInventory inventory = new RoomTypeInventory(
                "inv-1",
                "hotel-1",
                "type-1",
                LocalDate.now(),
                10,
                10
        );

        assertFalse(inventory.hasAvailability());
    }

    @Test
    void hasAvailabilityShouldReturnFalseWhenReservedIsMoreThanInventory() {
        RoomTypeInventory inventory = new RoomTypeInventory(
                "inv-1",
                "hotel-1",
                "type-1",
                LocalDate.now(),
                10,
                11
        );

        assertFalse(inventory.hasAvailability());
    }
}
