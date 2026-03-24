package com.fran.hotel.application.service;

import com.fran.hotel.domain.exception.RoomNotFoundException;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import com.fran.hotel.domain.port.RoomUseCase;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class RoomService implements RoomUseCase {
    
    private final RoomPersistencePort persistence;
    private final RoomTypeInventoryPersistencePort inventoryPersistence;

    public RoomService(RoomPersistencePort persistence, RoomTypeInventoryPersistencePort inventoryPersistence) {
        this.persistence = persistence;
        this.inventoryPersistence = inventoryPersistence;
    }

    @Override
    public Room getRoom(String hotelId, String roomId) {
        Room room = persistence.findByHotelIdAndRoomId(hotelId, roomId);
        if (room == null) {
            throw new RoomNotFoundException("Room with id " + roomId + " not found for hotel " + hotelId);
        }
        return room;
    }

    @Override
    public List<Room> getRooms(String hotelId) {
        return persistence.findByHotelId(hotelId);
    }

    @Override
    public Room addRoom(Room room) {
        Room savedRoom = persistence.saveRoom(room);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(365);

        List<RoomTypeInventory> existingInventories = inventoryPersistence.findByHotelIdAndRoomTypeIdAndDateBetween(
                room.hotelId(), room.typeId(), start, end);

        Map<LocalDate, RoomTypeInventory> inventoryByDate = existingInventories.stream()
                .collect(Collectors.toMap(RoomTypeInventory::date, Function.identity()));

        List<RoomTypeInventory> updatedInventories = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .map(date -> {
                    RoomTypeInventory inventory = inventoryByDate.get(date);
                    if (inventory != null) {
                        return new RoomTypeInventory(
                                inventory.id(),
                                inventory.hotelId(),
                                inventory.roomTypeId(),
                                inventory.date(),
                                inventory.totalInventory() + 1,
                                inventory.totalReserved()
                        );
                    } else {
                        return new RoomTypeInventory(
                                null, // new inventory, id will be generated
                                room.hotelId(),
                                room.typeId(),
                                date,
                                1, // New room, so inventory is 1
                                0
                        );
                    }
                })
                .collect(toList());

        inventoryPersistence.saveAll(updatedInventories);

        return savedRoom;
    }

    @Override
    public Room updateRoom(Room room) {
        return persistence.saveRoom(room);
    }

    @Override
    public void deleteRoom(String hotelId, String roomId) {
        persistence.deleteRoom(hotelId, roomId);
    }

}
