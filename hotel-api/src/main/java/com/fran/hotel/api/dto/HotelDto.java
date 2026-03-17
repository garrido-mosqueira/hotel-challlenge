package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
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

}
