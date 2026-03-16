package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.domain.port.ReservationUseCase;

import java.util.List;

public class ReservationService implements ReservationUseCase {

    private final ReservationPersistencePort persistence;

    public ReservationService(ReservationPersistencePort persistence) {
        this.persistence = persistence;
    }

    @Override
    public List<Reservation> getReservationsForUser(String userId) {
        return persistence.findByUserId(userId);
    }

    @Override
    public Reservation getReservation(String id) {
        return persistence.findById(id);
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        return persistence.save(reservation);
    }

    @Override
    public void cancelReservation(String id) {
        persistence.deleteById(id);
    }
}
