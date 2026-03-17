package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomDto {
    private String id;
    private String roomNumber;
    private String type;

    public RoomDto() {}

    public RoomDto(String id, String roomNumber, String type) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
    }

}
