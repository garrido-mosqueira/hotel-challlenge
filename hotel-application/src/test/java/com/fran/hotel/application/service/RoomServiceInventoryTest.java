package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomType;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.HotelPersistencePort;
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
    private HotelPersistencePort hotelPersistence;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        persistence = mock(RoomPersistencePort.class);
        inventoryPersistence = mock(RoomTypeInventoryPersistencePort.class);
        hotelPersistence = mock(HotelPersistencePort.class);
        roomService = new RoomService(persistence, inventoryPersistence, hotelPersistence);
    }

    @Test
    void addRoomShouldIncreaseInventoryStartingAfter30Days() {
        // Given
        String hotelId = "hotel-1";
        Room room = new Room(null, hotelId, RoomType.SINGLE, 1, "101", "Room 101", true);
        Room savedRoom = new Room("room-1", hotelId, RoomType.SINGLE, 1, "101", "Room 101", true);
        
        when(hotelPersistence.findById(hotelId)).thenReturn(new Hotel(hotelId, "Test Hotel", "Madrid"));
        when(persistence.saveRoom(room)).thenReturn(savedRoom);

        // The implementation uses LocalDate.now(), so we test relative to today.
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(365);

        // One existing inventory record inside the range, other dates have no inventory
        List<RoomTypeInventory> existingInventories = List.of(
                new RoomTypeInventory("inv-1", hotelId, RoomType.SINGLE, start.plusDays(10), 5, 2)
        );

        when(inventoryPersistence.findByHotelIdAndRoomTypeIdAndDateBetween(eq(hotelId), eq(RoomType.SINGLE), eq(start), eq(end)))
                .thenReturn(existingInventories);

        // When
        roomService.addRoom(room);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RoomTypeInventory>> captor = ArgumentCaptor.forClass(List.class);
        verify(inventoryPersistence).saveAll(captor.capture());
        List<RoomTypeInventory> updated = captor.getValue();

        // Ensure 366 dates are created/updated
        long expectedDays = start.datesUntil(end.plusDays(1)).count();
        assertThat(updated).hasSize((int) expectedDays);
        
        // The one that existed should be increased by 1 (from 5 to 6)
        RoomTypeInventory updatedExisting = updated.stream()
                .filter(inv -> "inv-1".equals(inv.id()))
                .findFirst()
                .orElseThrow();
        assertThat(updatedExisting.totalInventory()).isEqualTo(6);
        assertThat(updatedExisting.date()).isEqualTo(start.plusDays(10));

        // The ones that did not exist should be initialized to 1
        updated.stream()
                .filter(inv -> !"inv-1".equals(inv.id()))
                .forEach(inv -> assertThat(inv.totalInventory()).isEqualTo(1));
    }
}
