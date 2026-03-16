package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Room;

public interface RoomPersistencePort {
    Room findByHotelIdAndRoomId(String hotelId, String roomId);
    Room saveRoom(String hotelId, Room room);
    void deleteRoom(String hotelId, String roomId);
}
