package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelDto {

    private String id;
    private String name;
    private String city;

    public HotelDto(String id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

}
