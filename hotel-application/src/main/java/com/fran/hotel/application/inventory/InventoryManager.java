package com.fran.hotel.application.inventory;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.model.RoomType;
import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.domain.port.RoomTypeInventoryPersistencePort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InventoryManager {

    private static final int INVENTORY_PLANNING_DAYS = 365;
    private final RoomTypeInventoryPersistencePort inventoryPersistencePort;

    public InventoryManager(RoomTypeInventoryPersistencePort inventoryPersistencePort) {
        this.inventoryPersistencePort = inventoryPersistencePort;
    }

    public void initializeInventoryForNewRoom(Room room) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(INVENTORY_PLANNING_DAYS);

        List<RoomTypeInventory> existingInventories = fetchExistingInventories(
                room.hotelId(),
                room.typeId(),
                startDate,
                endDate
        );

        List<RoomTypeInventory> updatedInventories = generateInventoriesForDateRange(
                room,
                existingInventories,
                startDate,
                endDate
        );

        inventoryPersistencePort.saveAll(updatedInventories);
    }

    private List<RoomTypeInventory> fetchExistingInventories(
            String hotelId,
            RoomType roomTypeId,
            LocalDate startDate,
            LocalDate endDate) {
        return inventoryPersistencePort.findByHotelIdAndRoomTypeIdAndDateBetween(
                hotelId,
                roomTypeId,
                startDate,
                endDate
        );
    }

    private List<RoomTypeInventory> generateInventoriesForDateRange(
            Room room,
            List<RoomTypeInventory> existingInventories,
            LocalDate startDate,
            LocalDate endDate) {
        Map<LocalDate, RoomTypeInventory> inventoryByDate = existingInventories.stream()
                .collect(Collectors.toMap(RoomTypeInventory::date, Function.identity()));

        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> createOrUpdateInventory(room, inventoryByDate.get(date), date))
                .toList();
    }

    private RoomTypeInventory createOrUpdateInventory(
            Room room,
            RoomTypeInventory existingInventory,
            LocalDate date) {
        if (existingInventory != null) {
            return existingInventory.increaseInventory();
        } else {
            return RoomTypeInventory.createNew(room.hotelId(), room.typeId(), date);
        }
    }
}
