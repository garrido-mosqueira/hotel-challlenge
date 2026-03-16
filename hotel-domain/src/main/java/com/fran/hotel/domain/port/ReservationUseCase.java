package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Reservation;

import java.util.List;

public interface ReservationUseCase {
    List<Reservation> getReservationsForUser(String userId);

    Reservation getReservation(String id);

    Reservation createReservation(Reservation reservation);

    void cancelReservation(String id);
}
