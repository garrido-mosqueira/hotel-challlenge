package com.fran.hotel.application.service;

import com.fran.hotel.domain.exception.HotelNotFoundException;
import com.fran.hotel.domain.exception.RoomNotFoundException;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;
import com.fran.hotel.domain.port.RoomUseCase;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoomService implements RoomUseCase {

    private final RoomPersistencePort persistence;
    private final RoomTypeInventoryPersistencePort inventoryPersistence;
    private final HotelPersistencePort hotelPersistence;

    public RoomService(RoomPersistencePort persistence, RoomTypeInventoryPersistencePort inventoryPersistence, HotelPersistencePort hotelPersistence) {
        this.persistence = persistence;
        this.inventoryPersistence = inventoryPersistence;
        this.hotelPersistence = hotelPersistence;
    }

    @Override
    public Room getRoom(String hotelId, String roomId) {
        validateHotel(hotelId);
        return persistence.findByHotelIdAndRoomId(hotelId, roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room with id " + roomId + " not found for hotel " + hotelId));
    }

    @Override
    public List<Room> getRooms(String hotelId) {
        validateHotel(hotelId);
        return persistence.findByHotelId(hotelId);
    }

    @Override
    public Room addRoom(Room room) {
        validateHotel(room.hotelId());

        Room savedRoom = persistence.saveRoom(room);

        updateInventoryForNewRoom(room);

        return savedRoom;
    }

    private void updateInventoryForNewRoom(Room room) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(365);

        List<RoomTypeInventory> existingInventories = inventoryPersistence.findByHotelIdAndRoomTypeIdAndDateBetween(
                room.hotelId(), room.typeId(), start, end);

        Map<LocalDate, RoomTypeInventory> inventoryByDate = existingInventories.stream()
                .collect(Collectors.toMap(RoomTypeInventory::date, Function.identity()));

        List<RoomTypeInventory> updatedInventories = start.datesUntil(end.plusDays(1))
                .map(date -> buildRoomTypeInventory(room, inventoryByDate.get(date), date))
                .toList();

        inventoryPersistence.saveAll(updatedInventories);
    }

    private RoomTypeInventory buildRoomTypeInventory(Room room, RoomTypeInventory inventory, LocalDate date) {
        if (inventory != null) {
            return inventory.increaseInventory();
        } else {
            return RoomTypeInventory.createNew(room.hotelId(), room.typeId(), date);
        }
    }

    @Override
    public Room updateRoom(Room room) {
        validateHotel(room.hotelId());
        return persistence.saveRoom(room);
    }

    @Override
    public void deleteRoom(String hotelId, String roomId) {
        validateHotel(hotelId);
        persistence.deleteRoom(hotelId, roomId);
    }

    private void validateHotel(String hotelId) {
        if (hotelPersistence.findById(hotelId).isEmpty()) {
            throw new HotelNotFoundException("Hotel with ID " + hotelId + " not found");
        }
    }

}
