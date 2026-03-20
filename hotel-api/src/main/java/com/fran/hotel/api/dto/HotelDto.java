package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelDto {

    private String hotelId;
    private String name;
    private String city;

    public HotelDto(String hotelId, String name, String city) {
        this.hotelId = hotelId;
        this.name = name;
        this.city = city;
    }

}
