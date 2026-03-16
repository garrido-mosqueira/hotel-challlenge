package com.fran.hotel.api.dto;

import java.util.List;

public class HotelDto {
    private String id;
    private String name;
    private List<com.fran.hotel.domain.model.Room> rooms;

    public HotelDto() {}

    public HotelDto(String id, String name, List<com.fran.hotel.domain.model.Room> rooms) {
        this.id = id;
        this.name = name;
        this.rooms = rooms;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<com.fran.hotel.domain.model.Room> getRooms() { return rooms; }
    public void setRooms(List<com.fran.hotel.domain.model.Room> rooms) { this.rooms = rooms; }
}
