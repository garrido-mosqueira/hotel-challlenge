package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.persistence.entity.ReservationEntity;
import com.fran.hotel.persistence.mapper.ReservationEntityMapper;
import com.fran.hotel.persistence.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationPersistenceAdapter implements ReservationPersistencePort {

    private final ReservationRepository reservationRepository;
    private final ReservationEntityMapper reservationMapper;

    @Override
    public List<Reservation> findByUserId(String userId) {
        return reservationRepository.findByGuestId(userId)
                .stream()
                .map(reservationMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDomain);
    }

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = reservationMapper.toEntity(reservation);
        ReservationEntity saved = reservationRepository.save(entity);
        return reservationMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<Reservation> findOverlappingReservations(String roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        List<ReservationStatus> overlappingStatuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
        return reservationRepository.findOverlappingReservations(roomId, checkInDate, checkOutDate, overlappingStatuses)
                .stream()
                .map(reservationMapper::toDomain)
                .toList();
    }

}