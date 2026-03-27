package com.fran.hotel.application.service;

import com.fran.hotel.application.assembler.ReservationAssembler;
import com.fran.hotel.application.validator.AvailabilityChecker;
import com.fran.hotel.application.validator.ReservationValidator;
import com.fran.hotel.domain.exception.ReservationNotFoundException;
import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.*;

import java.util.List;

public class ReservationService implements ReservationUseCase {

    private final ReservationPersistencePort persistence;
    private final ReservationPaymentPort paymentPort;
    private final ReservationValidator reservationValidator;
    private final AvailabilityChecker availabilityChecker;
    private final ReservationAssembler reservationAssembler;

    public ReservationService(ReservationPersistencePort persistence,
                              ReservationPaymentPort paymentPort,
                              RoomPersistencePort roomPersistencePort,
                              RoomTypeInventoryPersistencePort roomTypeInventoryPersistencePort) {
        this.persistence = persistence;
        this.paymentPort = paymentPort;
        this.reservationValidator = new ReservationValidator(roomPersistencePort);
        this.availabilityChecker = new AvailabilityChecker(roomTypeInventoryPersistencePort, persistence);
        this.reservationAssembler = new ReservationAssembler();
    }

    @Override
    public List<Reservation> getReservationsForUser(String userId) {
        return persistence.findByUserId(userId);
    }

    @Override
    public Reservation getReservation(String id) {
        return persistence.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        Room room = reservationValidator.validateRoomExists(reservation.roomId());
        
        Reservation enrichedReservation = reservationAssembler.enrichReservationWithRoomName(reservation, room);
        
        availabilityChecker.validateInventoryAvailability(enrichedReservation, room);
        availabilityChecker.validateNoOverlappingReservations(enrichedReservation);
        
        Reservation savedReservation = persistence.save(enrichedReservation);
        return paymentPort.executeReservationPayment(savedReservation);
    }

    @Override
    public void cancelReservation(String id) {
        Reservation reservation = persistence.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + id));
        Reservation canceled = reservation.cancel();
        persistence.save(canceled);
    }

}
