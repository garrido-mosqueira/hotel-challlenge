package com.fran.hotel.api.dto;

public record HotelResponse(String id, String name, java.util.List<com.fran.hotel.domain.model.Room> rooms) {
}
