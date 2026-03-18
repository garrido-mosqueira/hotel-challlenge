package com.fran.hotel.domain.model;

public record Hotel(
    Long hotelId,
    String name,
    String address,
    String city
) {}
