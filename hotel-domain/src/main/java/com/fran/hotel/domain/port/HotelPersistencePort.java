package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Hotel;

public interface HotelPersistencePort {
    Hotel findById(String id);
    Hotel save(Hotel hotel);
    void deleteById(String id);
}
