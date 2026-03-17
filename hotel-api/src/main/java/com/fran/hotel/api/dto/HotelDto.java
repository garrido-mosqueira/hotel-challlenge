package com.fran.hotel.api.dto;

import com.fran.hotel.domain.model.Room;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HotelDto {

    private String id;
    private String name;
    private String city;
    private List<Room> rooms;

    public HotelDto() {}

    public HotelDto(String id, String name, String city, List<Room> rooms) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.rooms = rooms;
    }

}