package com.fran.hotel.api.configuration;

import com.fran.hotel.api.mapper.HotelMapper;
import com.fran.hotel.api.mapper.ReservationDTOMapper;
import com.fran.hotel.api.mapper.RoomDTOMapper;
import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.HotelUseCase;
import com.fran.hotel.domain.port.ReservationUseCase;
import com.fran.hotel.domain.port.RoomUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Configuration
public class HotelServiceConfiguration {

    @Bean
    public com.fran.hotel.domain.port.HotelPersistencePort hotelPersistencePort(com.fran.hotel.persistence.repository.HotelRepository repo) {
        return new com.fran.hotel.persistence.adapter.HotelPersistenceAdapter(repo);
    }

    @Bean
    public com.fran.hotel.domain.port.RoomPersistencePort roomPersistencePort(com.fran.hotel.persistence.repository.RoomRepository roomRepo, com.fran.hotel.persistence.repository.HotelRepository hotelRepo) {
        return new com.fran.hotel.persistence.adapter.RoomPersistenceAdapter(roomRepo, hotelRepo);
    }

    @Bean
    public com.fran.hotel.domain.port.ReservationPersistencePort reservationPersistencePort(com.fran.hotel.persistence.repository.ReservationRepository repo) {
        return new com.fran.hotel.persistence.adapter.ReservationPersistenceAdapter(repo);
    }

    @Bean
    public HotelUseCase hotelUseCase(com.fran.hotel.domain.port.HotelPersistencePort persistence) {
        return new com.fran.hotel.application.service.HotelService(persistence);
    }

    @Bean
    public RoomUseCase roomUseCase(com.fran.hotel.domain.port.RoomPersistencePort persistence) {
        return new com.fran.hotel.application.service.RoomService(persistence);
    }

    @Bean
    public ReservationUseCase reservationUseCase(com.fran.hotel.domain.port.ReservationPersistencePort persistence) {
        return new com.fran.hotel.application.service.ReservationService(persistence);
    }

    static class InMemoryHotelUseCase implements HotelUseCase {
        private final Map<String, Hotel> store = new ConcurrentHashMap<>();

        @Override
        public Hotel getHotel(String id) {
            return store.get(id);
        }

        @Override
        public Hotel addHotel(Hotel hotel) {
            store.put(hotel.id(), hotel);
            return hotel;
        }

        @Override
        public Hotel updateHotel(Hotel hotel) {
            store.put(hotel.id(), hotel);
            return hotel;
        }

        @Override
        public void deleteHotel(String id) {
            store.remove(id);
        }
    }

    static class InMemoryRoomUseCase implements RoomUseCase {
        private final Map<String, Hotel> hotels = new ConcurrentHashMap<>();

        @Override
        public Room getRoom(String hotelId, String roomId) {
            Hotel h = hotels.get(hotelId);
            if (h == null) return null;
            return h.rooms().stream().filter(r -> r.id().equals(roomId)).findFirst().orElse(null);
        }

        @Override
        public Room addRoom(String hotelId, Room room) {
            hotels.compute(hotelId, (k, v) -> {
                if (v == null) return new Hotel(hotelId, "", new ArrayList<>(List.of(room)));
                List<Room> rooms = new ArrayList<>(v.rooms());
                rooms.add(room);
                return new Hotel(v.id(), v.name(), rooms);
            });
            return room;
        }

        @Override
        public Room updateRoom(String hotelId, Room room) {
            hotels.computeIfPresent(hotelId, (k, v) -> {
                List<Room> rooms = v.rooms().stream().map(r -> r.id().equals(room.id()) ? room : r).collect(Collectors.toList());
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

    static class InMemoryReservationUseCase implements ReservationUseCase {
        private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

        @Override
        public java.util.List<Reservation> getReservationsForUser(String userId) {
            return reservations.values().stream().filter(r -> r.guest() != null && userId.equals(r.guest().id())).collect(Collectors.toList());
        }

        @Override
        public Reservation getReservation(String id) {
            return reservations.get(id);
        }

        @Override
        public Reservation createReservation(Reservation reservation) {
            reservations.put(reservation.id(), reservation);
            return reservation;
        }

        @Override
        public void cancelReservation(String id) {
            reservations.remove(id);
        }
    }
}
