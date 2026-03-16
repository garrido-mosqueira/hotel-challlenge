package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.port.HotelPersistencePort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryHotelPersistenceAdapter implements HotelPersistencePort {
    private final Map<String, Hotel> store = new ConcurrentHashMap<>();

    @Override
    public Hotel findById(String id) {
        return store.get(id);
    }

    @Override
    public Hotel save(Hotel hotel) {
        store.put(hotel.id(), hotel);
        return hotel;
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
