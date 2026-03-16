package com.fran.hotel.domain.model;

import java.util.List;

public record Hotel(String id, String name, List<Room> rooms) {
}
