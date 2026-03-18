package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {

    @Query("SELECT r FROM RoomEntity r WHERE r.id = :roomId AND r.hotel.id = :hotelId")
    RoomEntity findByHotelIdAndRoomId(@Param("hotelId") String hotelId, @Param("roomId") UUID roomId);

    List<RoomEntity> findByHotelId(String hotelId);

}
