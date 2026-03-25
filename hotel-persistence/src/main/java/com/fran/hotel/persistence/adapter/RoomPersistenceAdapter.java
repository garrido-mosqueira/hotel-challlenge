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

import java.util.List;

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
    public Room findById(String roomId) {
        return roomRepository.findById(roomId)
                .map(roomMapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<Room> findByHotelId(String hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(roomMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        HotelEntity hotel = hotelRepository.findById(room.hotelId())
                .orElse(null);

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