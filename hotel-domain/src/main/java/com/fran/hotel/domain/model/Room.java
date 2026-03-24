package com.fran.hotel.domain.model;

public record Room(
    String id,
    String hotelId,
    String typeId,
    int floor,
    String number,
    String name,
    boolean isAvailable
) {

    public Room withId(String id) {
        return new Room(id, hotelId, typeId, floor, number, name, isAvailable);
    }

    public Room withHotelId(String hotelId) {
        return new Room(id, hotelId, typeId, floor, number, name, isAvailable);

    }

}
