package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomDto {

    private String roomId;
    private String hotelId;
    private String roomTypeId;
    private int floor;
    private String number;
    private String name;
    private boolean isAvailable;

    public RoomDto(String roomId, String hotelId, String roomTypeId, int floor, String number, String name, boolean isAvailable) {
        this.roomId = roomId;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.floor = floor;
        this.number = number;
        this.name = name;
        this.isAvailable = isAvailable;
    }

}
