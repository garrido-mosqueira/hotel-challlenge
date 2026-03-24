package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Room;

import java.util.List;

public interface RoomUseCase {
    Room getRoom(String hotelId, String roomId);

    List<Room> getRooms(String hotelId);

    Room addRoom(Room room);

    Room updateRoom(Room room);

    void deleteRoom(String hotelId, String roomId);
}
