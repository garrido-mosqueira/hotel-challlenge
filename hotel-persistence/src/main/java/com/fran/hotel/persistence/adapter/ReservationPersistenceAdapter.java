package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.persistence.entity.ReservationEntity;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.persistence.mapper.ReservationEntityMapper;
import com.fran.hotel.persistence.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    @Override
    public Reservation findById(String id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDomain)
                .orElse(null);
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

}