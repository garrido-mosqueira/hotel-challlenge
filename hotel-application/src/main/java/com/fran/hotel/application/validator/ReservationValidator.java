package com.fran.hotel.application.validator;

import com.fran.hotel.domain.exception.RoomNotFoundException;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomPersistencePort;

public class ReservationValidator {

    private final RoomPersistencePort roomPersistencePort;

    public ReservationValidator(RoomPersistencePort roomPersistencePort) {
        this.roomPersistencePort = roomPersistencePort;
    }

    public Room validateRoomExists(String roomId) {
        return roomPersistencePort.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
    }
}
