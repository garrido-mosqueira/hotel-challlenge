package com.fran.hotel.application.assembler;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.Room;

public class ReservationAssembler {

    public Reservation enrichReservationWithRoomName(Reservation reservation, Room room) {
        return new Reservation(
                reservation.id(),
                reservation.guestId(),
                reservation.roomId(),
                room.name(),
                reservation.checkInDate(),
                reservation.checkOutDate(),
                reservation.status()
        );
    }
}
