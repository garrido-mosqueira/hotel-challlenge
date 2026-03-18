package com.fran.hotel.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelDto {

    private Long hotelId;
    private String name;
    private String address;
    private String city;

    public HotelDto(Long hotelId, String name, String address, String city) {
        this.hotelId = hotelId;
        this.name = name;
        this.address = address;
        this.city = city;
    }

}
