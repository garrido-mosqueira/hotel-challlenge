package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.port.ReservationPaymentPort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.domain.port.ReservationUseCase;

import java.util.List;

public class ReservationService implements ReservationUseCase {

    private final ReservationPersistencePort persistence;
    private final ReservationPaymentPort paymentPort;

    public ReservationService(ReservationPersistencePort persistence, ReservationPaymentPort paymentPort) {
        this.persistence = persistence;
        this.paymentPort = paymentPort;
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
        Reservation savedReservation = persistence.save(reservation);
        return paymentPort.executeReservationPayment(savedReservation);
    }

    @Override
    public void cancelReservation(String id) {
        Reservation reservation = persistence.findById(id);
        if (reservation != null) {
            paymentPort.cancelReservationPayment(id);
            Reservation canceled = reservation.withStatus(ReservationStatus.CANCELLED);
            persistence.save(canceled);
        }
    }
}
