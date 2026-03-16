package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Reservation;
import java.util.List;

public interface ReservationPersistencePort {
    List<Reservation> findByUserId(String userId);
    Reservation findById(String id);
    Reservation save(Reservation reservation);
    void deleteById(String id);
}
