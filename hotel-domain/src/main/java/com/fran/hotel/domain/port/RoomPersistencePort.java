package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Room;

import java.util.List;

public interface RoomPersistencePort {
    Room findByHotelIdAndRoomId(String hotelId, String roomId);
    Room findById(String roomId);
    List<Room> findByHotelId(String hotelId);
    Room saveRoom(String hotelId, Room room);
    void deleteRoom(String hotelId, String roomId);
}
