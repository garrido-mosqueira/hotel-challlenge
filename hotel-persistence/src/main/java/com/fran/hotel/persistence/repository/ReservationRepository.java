package com.fran.hotel.persistence.repository;

import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, String> {

    List<ReservationEntity> findByGuestId(String guestId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.roomId = :roomId AND r.status IN :statuses AND " +
           "((r.checkInDate < :checkOutDate AND r.checkOutDate >= :checkInDate))")
    List<ReservationEntity> findOverlappingReservations(
            @Param("roomId") String roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<ReservationStatus> statuses);

}