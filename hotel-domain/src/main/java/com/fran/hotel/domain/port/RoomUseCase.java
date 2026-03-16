package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Room;

public interface RoomUseCase {
    Room getRoom(String hotelId, String roomId);

    Room addRoom(String hotelId, Room room);

    Room updateRoom(String hotelId, Room room);

    void deleteRoom(String hotelId, String roomId);
}
