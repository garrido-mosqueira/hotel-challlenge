package com.fran.hotel.persistence.repository;

import com.fran.hotel.domain.model.RoomType;
import com.fran.hotel.persistence.entity.RoomTypeInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RoomTypeInventoryRepository extends JpaRepository<RoomTypeInventoryEntity, String> {
    List<RoomTypeInventoryEntity> findByHotelIdAndRoomTypeIdAndDateBetween(String hotelId, RoomType roomTypeId, LocalDate startDate, LocalDate endDate);
}
