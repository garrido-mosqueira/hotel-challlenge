package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.entity.RoomEntity;
import com.fran.hotel.persistence.repository.HotelRepository;
import com.fran.hotel.persistence.repository.RoomRepository;
import com.fran.hotel.persistence.mapper.RoomEntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomPersistenceAdapter implements RoomPersistencePort {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomEntityMapper roomMapper;

    @Override
    public Room findByHotelIdAndRoomId(String hotelId, String roomId) {
        RoomEntity roomEntity = roomRepository.findByHotelIdAndRoomId(hotelId, roomId);
        if (roomEntity == null) return null;
        return roomMapper.toDomain(roomEntity);
    }

    @Override
    @Transactional
    public Room saveRoom(String hotelId, Room room) {
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseGet(() -> {
                    HotelEntity newHotel = new HotelEntity(hotelId, "");
                    return hotelRepository.save(newHotel);
                });
        RoomEntity roomEntity = roomMapper.toEntity(room);
        roomEntity.setHotel(hotel);
        RoomEntity saved = roomRepository.save(roomEntity);
        return roomMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteRoom(String hotelId, String roomId) {
        RoomEntity roomEntity = roomRepository.findByHotelIdAndRoomId(hotelId, roomId);
        if (roomEntity != null) {
            roomRepository.delete(roomEntity);
        }
    }

}
