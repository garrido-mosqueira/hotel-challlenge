package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findByGuestId(String guestId);

}