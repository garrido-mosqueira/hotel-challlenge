package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.entity.RoomEntity;
import com.fran.hotel.persistence.repository.HotelRepository;
import com.fran.hotel.persistence.repository.RoomRepository;
import jakarta.transaction.Transactional;

public class RoomPersistenceAdapter implements RoomPersistencePort {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomPersistenceAdapter(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Room findByHotelIdAndRoomId(String hotelId, String roomId) {
        RoomEntity re = roomRepository.findByHotelIdAndRoomId(hotelId, roomId);
        if (re == null) return null;
        return new Room(re.getId(), re.getRoomNumber(), re.getType());
    }

    @Override
    @Transactional
    public Room saveRoom(String hotelId, Room room) {
        HotelEntity hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel == null) {
            hotel = new HotelEntity(hotelId, "");
            hotelRepository.save(hotel);
        }
        RoomEntity re = new RoomEntity(room.id(), room.roomNumber(), room.type(), hotel);
        re = roomRepository.save(re);
        return new Room(re.getId(), re.getRoomNumber(), re.getType());
    }

    @Override
    @Transactional
    public void deleteRoom(String hotelId, String roomId) {
        RoomEntity re = roomRepository.findByHotelIdAndRoomId(hotelId, roomId);
        if (re != null) roomRepository.delete(re);
    }
}
