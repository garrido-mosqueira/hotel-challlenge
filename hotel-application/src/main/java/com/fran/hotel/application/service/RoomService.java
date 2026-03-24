package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import com.fran.hotel.domain.port.RoomUseCase;

import java.time.LocalDate;
import java.util.List;

public class RoomService implements RoomUseCase {
    
    private final RoomPersistencePort persistence;
    private final RoomTypeInventoryPersistencePort inventoryPersistence;

    public RoomService(RoomPersistencePort persistence, RoomTypeInventoryPersistencePort inventoryPersistence) {
        this.persistence = persistence;
        this.inventoryPersistence = inventoryPersistence;
    }

    @Override
    public Room getRoom(String hotelId, String roomId) {
        return persistence.findByHotelIdAndRoomId(hotelId, roomId);
    }

    @Override
    public List<Room> getRooms(String hotelId) {
        return persistence.findByHotelId(hotelId);
    }

    @Override
    public Room addRoom(Room room) {
        Room savedRoom = persistence.saveRoom(room);

        // Increase inventory for the added room type
        // Example: Update inventory for the next 30 days
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(365);

        List<RoomTypeInventory> inventories = inventoryPersistence.findByHotelIdAndRoomTypeIdAndDateBetween(
                room.hotelId(), room.typeId(), start, end);

        List<RoomTypeInventory> updatedInventories = inventories.stream()
                .map(inv -> new RoomTypeInventory(
                        inv.id(),
                        inv.hotelId(),
                        inv.roomTypeId(),
                        inv.date(),
                        inv.totalInventory() + 1, // Increase capacity
                        inv.totalReserved()
                ))
                .toList();

        inventoryPersistence.saveAll(updatedInventories);

        return savedRoom;
    }

    @Override
    public Room updateRoom(Room room) {
        return persistence.saveRoom(room);
    }

    @Override
    public void deleteRoom(String hotelId, String roomId) {
        persistence.deleteRoom(hotelId, roomId);
    }

}
