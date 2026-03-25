package com.fran.hotel.application.validator;

import com.fran.hotel.domain.exception.ReservationAvailabilityException;
import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class AvailabilityChecker {

    private final RoomTypeInventoryPersistencePort inventoryPersistencePort;
    private final ReservationPersistencePort reservationPersistencePort;

    public AvailabilityChecker(RoomTypeInventoryPersistencePort inventoryPersistencePort,
                               ReservationPersistencePort reservationPersistencePort) {
        this.inventoryPersistencePort = inventoryPersistencePort;
        this.reservationPersistencePort = reservationPersistencePort;
    }

    public void validateInventoryAvailability(Reservation reservation, Room room) {
        List<RoomTypeInventory> inventories = inventoryPersistencePort
                .findByHotelIdAndRoomTypeIdAndDateBetween(
                        room.hotelId(),
                        room.typeId(),
                        reservation.checkInDate(),
                        reservation.checkOutDate().minusDays(1)
                );

        long numberOfNights = calculateNumberOfNights(reservation);
        validateInventoryCoverage(inventories.size(), numberOfNights);
        validateInventoryAvailable(inventories);
    }

    public void validateNoOverlappingReservations(Reservation reservation) {
        List<Reservation> overlapping = reservationPersistencePort.findOverlappingReservations(
                reservation.roomId(),
                reservation.checkInDate(),
                reservation.checkOutDate()
        );

        if (!overlapping.isEmpty()) {
            throw new ReservationAvailabilityException("Room is already reserved for the selected dates");
        }
    }

    private long calculateNumberOfNights(Reservation reservation) {
        return ChronoUnit.DAYS.between(reservation.checkInDate(), reservation.checkOutDate());
    }

    private void validateInventoryCoverage(int inventoryCount, long nightsCount) {
        if (inventoryCount < nightsCount) {
            throw new ReservationAvailabilityException("Missing inventory records for the selected dates");
        }
    }

    private void validateInventoryAvailable(List<RoomTypeInventory> inventories) {
        boolean hasAvailability = inventories.stream().allMatch(RoomTypeInventory::hasAvailability);
        if (!hasAvailability) {
            throw new ReservationAvailabilityException("No availability for the selected dates");
        }
    }
}
