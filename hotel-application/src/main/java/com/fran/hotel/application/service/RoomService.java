package com.fran.hotel.application.service;

import com.fran.hotel.application.inventory.InventoryManager;
import com.fran.hotel.application.validator.HotelValidator;
import com.fran.hotel.domain.exception.RoomNotFoundException;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import com.fran.hotel.domain.port.RoomUseCase;

import java.util.List;

public class RoomService implements RoomUseCase {

    private final RoomPersistencePort roomPersistence;
    private final HotelValidator hotelValidator;
    private final InventoryManager inventoryManager;

    public RoomService(RoomPersistencePort roomPersistence,
                       RoomTypeInventoryPersistencePort inventoryPersistence,
                       HotelPersistencePort hotelPersistence) {
        this.roomPersistence = roomPersistence;
        this.hotelValidator = new HotelValidator(hotelPersistence);
        this.inventoryManager = new InventoryManager(inventoryPersistence);
    }

    @Override
    public Room getRoom(String hotelId, String roomId) {
        hotelValidator.validateHotelExists(hotelId);
        return roomPersistence.findByHotelIdAndRoomId(hotelId, roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room with id " + roomId + " not found for hotel " + hotelId));
    }

    @Override
    public List<Room> getRooms(String hotelId) {
        hotelValidator.validateHotelExists(hotelId);
        return roomPersistence.findByHotelId(hotelId);
    }

    @Override
    public Room addRoom(Room room) {
        hotelValidator.validateHotelExists(room.hotelId());
        
        Room savedRoom = roomPersistence.saveRoom(room);
        inventoryManager.initializeInventoryForNewRoom(room);
        
        return savedRoom;
    }

    @Override
    public Room updateRoom(Room room) {
        hotelValidator.validateHotelExists(room.hotelId());
        return roomPersistence.saveRoom(room);
    }

    @Override
    public void deleteRoom(String hotelId, String roomId) {
        hotelValidator.validateHotelExists(hotelId);
        roomPersistence.deleteRoom(hotelId, roomId);
    }

}
