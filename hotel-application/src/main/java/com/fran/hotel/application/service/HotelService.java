package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.domain.port.HotelUseCase;

public class HotelService implements HotelUseCase {

    private final HotelPersistencePort persistence;

    public HotelService(HotelPersistencePort persistence) {
        this.persistence = persistence;
    }

    @Override
    public Hotel getHotel(String id) {
        return persistence.findById(id);
    }

    @Override
    public Hotel addHotel(Hotel hotel) {
        return persistence.save(hotel);
    }

    @Override
    public Hotel updateHotel(Hotel hotel) {
        return persistence.save(hotel);
    }

    @Override
    public void deleteHotel(String id) {
        persistence.deleteById(id);
    }

}
