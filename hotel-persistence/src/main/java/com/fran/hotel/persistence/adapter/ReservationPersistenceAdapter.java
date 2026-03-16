package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Guest;
import com.fran.hotel.domain.model.Rate;
import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.persistence.entity.ReservationEntity;
import com.fran.hotel.persistence.repository.ReservationRepository;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationPersistenceAdapter implements ReservationPersistencePort {

    private final ReservationRepository repository;

    public ReservationPersistenceAdapter(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reservation> findByUserId(String userId) {
        return repository.findByGuestId(userId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Reservation findById(String id) {
        return repository.findById(id).map(this::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        ReservationEntity e = toEntity(reservation);
        e = repository.save(e);
        return toDomain(e);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Reservation toDomain(ReservationEntity e) {
        Guest guest = new Guest(e.getGuestId(), "", "", "");
        return new Reservation(e.getId(), guest, new com.fran.hotel.domain.model.Room(e.getRoomId(), "", ""), e.getCheckInDate(), e.getCheckOutDate(), new Rate(e.getRateId(), "", null), e.getStatus());
    }

    private ReservationEntity toEntity(Reservation r) {
        return new ReservationEntity(r.id(), r.guest() != null ? r.guest().id() : null, r.room() != null ? r.room().id() : null, r.rate() != null ? r.rate().id() : null, r.checkInDate(), r.checkOutDate(), r.status());
    }
}
