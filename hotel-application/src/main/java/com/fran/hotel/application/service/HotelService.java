package com.fran.hotel.application.service;

import com.fran.hotel.domain.exception.HotelNotFoundException;
import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.domain.port.HotelUseCase;

import java.util.List;

public class HotelService implements HotelUseCase {

    private final HotelPersistencePort persistence;

    public HotelService(HotelPersistencePort persistence) {
        this.persistence = persistence;
    }

    @Override
    public Hotel getHotel(String id) {
        Hotel hotel = persistence.findById(id);
        if (hotel == null) {
            throw new HotelNotFoundException("Hotel not found with id: " + id);
        }
        return hotel;
    }

    @Override
    public List<Hotel> getHotels() {
        return persistence.findAll();
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

    @Override
    public List<Hotel> searchHotels(String city) {
        return persistence.findByCity(city);
    }

}