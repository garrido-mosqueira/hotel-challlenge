package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.entity.RoomEntity;
import com.fran.hotel.persistence.repository.HotelRepository;
import jakarta.transaction.Transactional;

import java.util.stream.Collectors;

public class HotelPersistenceAdapter implements HotelPersistencePort {

    private final HotelRepository repository;

    public HotelPersistenceAdapter(HotelRepository repository) {
        this.repository = repository;
    }

    @Override
    public Hotel findById(String id) {
        return repository.findById(id).map(this::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public Hotel save(Hotel hotel) {
        HotelEntity entity = toEntity(hotel);
        entity = repository.save(entity);
        return toDomain(entity);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Hotel toDomain(HotelEntity e) {
        var rooms = e.getRooms().stream().map(r -> new Room(r.getId(), r.getRoomNumber(), r.getType())).collect(Collectors.toList());
        return new Hotel(e.getId(), e.getName(), rooms);
    }

    private HotelEntity toEntity(Hotel h) {
        HotelEntity e = new HotelEntity(h.id(), h.name());
        var rooms = h.rooms().stream().map(r -> {
            RoomEntity re = new RoomEntity(r.id(), r.roomNumber(), r.type(), e);
            return re;
        }).collect(Collectors.toList());
        e.setRooms(rooms);
        return e;
    }
}
