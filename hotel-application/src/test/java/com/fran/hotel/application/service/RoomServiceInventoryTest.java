package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RoomServiceInventoryTest {

    private RoomPersistencePort persistence;
    private RoomTypeInventoryPersistencePort inventoryPersistence;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        persistence = mock(RoomPersistencePort.class);
        inventoryPersistence = mock(RoomTypeInventoryPersistencePort.class);
        roomService = new RoomService(persistence, inventoryPersistence);
    }

    @Test
    void addRoomShouldIncreaseInventoryStartingAfter30Days() {
        // Given
        String hotelId = "hotel-1";
        Room room = new Room(null, hotelId, "type-1", 1, "101", "Room 101", true);
        Room savedRoom = room.withId("room-1");
        when(persistence.saveRoom(hotelId, room)).thenReturn(savedRoom);

        LocalDate start = LocalDate.now().plusDays(31);
        LocalDate end = start.plusDays(365);

        // Only one existing inventory record, but 366 dates in the range
        List<RoomTypeInventory> existingInventories = List.of(
                new RoomTypeInventory("inv-1", hotelId, "type-1", start, 5, 2)
        );

        when(inventoryPersistence.findByHotelIdAndRoomTypeIdAndDateBetween(eq(hotelId), eq("type-1"), any(), any()))
                .thenReturn(existingInventories);

        // When
        roomService.addRoom(hotelId, room);

        // Then
        ArgumentCaptor<List<RoomTypeInventory>> captor = ArgumentCaptor.forClass(List.class);
        verify(inventoryPersistence).saveAll(captor.capture());
        List<RoomTypeInventory> updated = captor.getValue();

        // 366 dates in [start, end] inclusive? 
        // start to end is start.datesUntil(end.plusDays(1))
        long expectedDays = start.datesUntil(end.plusDays(1)).count();
        assertThat(updated).hasSize((int) expectedDays);
        
        // The one that existed should be 6
        RoomTypeInventory updatedExisting = updated.stream()
                .filter(inv -> "inv-1".equals(inv.id()))
                .findFirst()
                .orElseThrow();
        assertThat(updatedExisting.totalInventory()).isEqualTo(6);

        // Others should be 1
        updated.stream()
                .filter(inv -> !"inv-1".equals(inv.id()))
                .forEach(inv -> assertThat(inv.totalInventory()).isEqualTo(1));

        verify(inventoryPersistence).findByHotelIdAndRoomTypeIdAndDateBetween(eq(hotelId), eq("type-1"), eq(start), eq(end));
    }
}
