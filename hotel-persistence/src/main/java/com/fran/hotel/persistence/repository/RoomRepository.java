package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<RoomEntity, String> {

    @Query("SELECT r FROM RoomEntity r WHERE r.id = :roomId AND r.hotel.id = :hotelId")
    RoomEntity findByHotelIdAndRoomId(@Param("hotelId") String hotelId, @Param("roomId") String roomId);

}
