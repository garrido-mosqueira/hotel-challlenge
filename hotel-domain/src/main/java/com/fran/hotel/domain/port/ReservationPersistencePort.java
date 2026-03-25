package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationPersistencePort {
    List<Reservation> findByUserId(String userId);
    Optional<Reservation> findById(String id);
    Reservation save(Reservation reservation);
    void deleteById(String id);
    List<Reservation> findOverlappingReservations(String roomId, LocalDate checkInDate, LocalDate checkOutDate);
}
