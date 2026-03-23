package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomDto {

    private String id;
    private String hotelId;
    private String typeId;
    private int floor;
    private String number;
    private String name;
    private boolean isAvailable;

    public RoomDto(String id, String hotelId, String typeId, int floor, String number, String name, boolean isAvailable) {
        this.id = id;
        this.hotelId = hotelId;
        this.typeId = typeId;
        this.floor = floor;
        this.number = number;
        this.name = name;
        this.isAvailable = isAvailable;
    }

}
