package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Room;

import java.util.List;

public interface RoomUseCase {
    Room getRoom(String hotelId, String roomId);

    List<Room> getRooms(String hotelId);

    Room addRoom(String hotelId, Room room);

    Room updateRoom(String hotelId, Room room);

    void deleteRoom(String hotelId, String roomId);
}
