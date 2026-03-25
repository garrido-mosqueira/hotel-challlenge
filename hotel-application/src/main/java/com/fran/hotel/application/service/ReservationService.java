package com.fran.hotel.application.service;

import com.fran.hotel.domain.exception.ReservationAvailabilityException;
import com.fran.hotel.domain.exception.RoomNotFoundException;
import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.*;

import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReservationService implements ReservationUseCase {

    private final ReservationPersistencePort persistence;
    private final ReservationPaymentPort paymentPort;
    private final RoomPersistencePort roomPersistencePort;
    private final RoomTypeInventoryPersistencePort roomTypeInventoryPersistencePort;

    public ReservationService(ReservationPersistencePort persistence, ReservationPaymentPort paymentPort, RoomPersistencePort roomPersistencePort, RoomTypeInventoryPersistencePort roomTypeInventoryPersistencePort) {
        this.persistence = persistence;
        this.paymentPort = paymentPort;
        this.roomPersistencePort = roomPersistencePort;
        this.roomTypeInventoryPersistencePort = roomTypeInventoryPersistencePort;
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
        Room room = roomPersistencePort.findById(reservation.roomId());
        if (room == null) {
            throw new RoomNotFoundException("Room not found");
        }

        List<RoomTypeInventory> inventories = roomTypeInventoryPersistencePort.findByHotelIdAndRoomTypeIdAndDateBetween(
                room.hotelId(),
                room.typeId(),
                reservation.checkInDate(),
                reservation.checkOutDate().minusDays(1)
        );

        long nights = DAYS.between(reservation.checkInDate(), reservation.checkOutDate());
        if (inventories.size() < nights) {
            throw new ReservationAvailabilityException("Missing inventory records for the selected dates");
        }

        boolean hasAvailability = inventories.stream().allMatch(RoomTypeInventory::hasAvailability);

        if (!hasAvailability) {
            throw new ReservationAvailabilityException("No availability for the selected dates");
        }

        Reservation savedReservation = persistence.save(reservation);
        return paymentPort.executeReservationPayment(savedReservation);
    }

    @Override
    public void cancelReservation(String id) {
        Reservation reservation = persistence.findById(id);
        if (reservation != null) {
            paymentPort.cancelReservationPayment(id);
            Reservation canceled = reservation.cancel();
            persistence.save(canceled);
        }
    }

}
