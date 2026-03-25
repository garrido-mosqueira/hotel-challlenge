package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Room;

import java.util.List;
import java.util.Optional;

public interface RoomPersistencePort {
    Optional<Room> findByHotelIdAndRoomId(String hotelId, String roomId);
    Optional<Room> findById(String roomId);
    List<Room> findByHotelId(String hotelId);
    Room saveRoom(Room room);
    void deleteRoom(String hotelId, String roomId);
}
