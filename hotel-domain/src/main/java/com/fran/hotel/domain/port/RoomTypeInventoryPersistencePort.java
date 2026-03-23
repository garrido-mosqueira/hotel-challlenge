package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.RoomTypeInventory;

import java.time.LocalDate;
import java.util.List;

public interface RoomTypeInventoryPersistencePort {

    List<RoomTypeInventory> findByHotelIdAndRoomTypeIdAndDateBetween(String hotelId, String roomTypeId, LocalDate startDate, LocalDate endDate);

    void saveAll(List<RoomTypeInventory> inventories);

}
