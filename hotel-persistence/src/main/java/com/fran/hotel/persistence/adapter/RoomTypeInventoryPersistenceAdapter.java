package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import com.fran.hotel.persistence.mapper.RoomTypeInventoryEntityMapper;
import com.fran.hotel.persistence.repository.RoomTypeInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomTypeInventoryPersistenceAdapter implements RoomTypeInventoryPersistencePort {

    private final RoomTypeInventoryRepository repository;
    private final RoomTypeInventoryEntityMapper mapper;

    @Override
    public List<RoomTypeInventory> findByHotelIdAndRoomTypeIdAndDateBetween(String hotelId, String roomTypeId, LocalDate startDate, LocalDate endDate) {
        return repository.findByHotelIdAndRoomTypeIdAndDateBetween(hotelId, roomTypeId, startDate, endDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
