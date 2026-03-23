package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.RoomTypeInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RoomTypeInventoryRepository extends JpaRepository<RoomTypeInventoryEntity, UUID> {
    List<RoomTypeInventoryEntity> findByHotelIdAndRoomTypeIdAndDateBetween(String hotelId, String roomTypeId, LocalDate startDate, LocalDate endDate);
}
