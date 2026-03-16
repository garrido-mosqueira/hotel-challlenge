package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomPersistencePort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryRoomPersistenceAdapter implements RoomPersistencePort {
    private final Map<String, Hotel> hotels = new ConcurrentHashMap<>();

    @Override
    public Room findByHotelIdAndRoomId(String hotelId, String roomId) {
        Hotel h = hotels.get(hotelId);
        if (h == null) return null;
        return h.rooms().stream().filter(r -> r.id().equals(roomId)).findFirst().orElse(null);
    }

    @Override
    public Room saveRoom(String hotelId, Room room) {
        hotels.compute(hotelId, (k, v) -> {
            if (v == null) return new Hotel(hotelId, "", new ArrayList<>(List.of(room)));
            List<Room> rooms = new ArrayList<>(v.rooms());
            rooms.add(room);
            return new Hotel(v.id(), v.name(), rooms);
        });
        return room;
    }

    @Override
    public void deleteRoom(String hotelId, String roomId) {
        hotels.computeIfPresent(hotelId, (k, v) -> {
            List<Room> rooms = v.rooms().stream().filter(r -> !r.id().equals(roomId)).collect(Collectors.toList());
            return new Hotel(v.id(), v.name(), rooms);
        });
    }
}
