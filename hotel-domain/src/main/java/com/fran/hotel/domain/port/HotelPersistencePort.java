package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Hotel;

import java.util.List;

public interface HotelPersistencePort {
    Hotel findById(String id);

    List<Hotel> findAll();

    Hotel save(Hotel hotel);

    void deleteById(String id);
}
