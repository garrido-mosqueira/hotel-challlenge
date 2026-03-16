package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.port.ReservationPersistencePort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryReservationPersistenceAdapter implements ReservationPersistencePort {
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    @Override
    public List<Reservation> findByUserId(String userId) {
        return reservations.values().stream().filter(r -> r.guest() != null && userId.equals(r.guest().id())).collect(Collectors.toList());
    }

    @Override
    public Reservation findById(String id) {
        return reservations.get(id);
    }

    @Override
    public Reservation save(Reservation reservation) {
        reservations.put(reservation.id(), reservation);
        return reservation;
    }

    @Override
    public void deleteById(String id) {
        reservations.remove(id);
    }
}
