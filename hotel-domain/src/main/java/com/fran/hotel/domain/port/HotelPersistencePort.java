package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Hotel;

import java.util.List;
import java.util.Optional;

public interface HotelPersistencePort {

    Optional<Hotel> findById(String id);

    List<Hotel> findAll();

    Hotel save(Hotel hotel);

    void deleteById(String id);

    List<Hotel> findByCity(String city);

}
