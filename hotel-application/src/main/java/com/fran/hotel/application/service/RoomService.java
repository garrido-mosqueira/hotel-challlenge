package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomUseCase;

public class RoomService implements RoomUseCase {

    private final RoomPersistencePort persistence;

    public RoomService(RoomPersistencePort persistence) {
        this.persistence = persistence;
    }

    @Override
    public Room getRoom(String hotelId, String roomId) {
        return persistence.findByHotelIdAndRoomId(hotelId, roomId);
    }

    @Override
    public Room addRoom(String hotelId, Room room) {
        return persistence.saveRoom(hotelId, room);
    }

    @Override
    public Room updateRoom(String hotelId, Room room) {
        return persistence.saveRoom(hotelId, room);
    }

    @Override
    public void deleteRoom(String hotelId, String roomId) {
        persistence.deleteRoom(hotelId, roomId);
    }
}
