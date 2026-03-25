package com.fran.hotel.domain.model;

public record Hotel(
    String id,
    String name,
    String city
) {

    public Hotel withId(String id) {
        return new Hotel(id, name, city);
    }

}