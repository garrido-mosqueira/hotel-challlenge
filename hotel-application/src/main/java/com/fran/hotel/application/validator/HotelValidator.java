package com.fran.hotel.application.validator;

import com.fran.hotel.domain.exception.HotelNotFoundException;
import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.port.HotelPersistencePort;

public class HotelValidator {

    private final HotelPersistencePort hotelPersistencePort;

    public HotelValidator(HotelPersistencePort hotelPersistencePort) {
        this.hotelPersistencePort = hotelPersistencePort;
    }

    public Hotel validateHotelExists(String hotelId) {
        return hotelPersistencePort.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with ID " + hotelId + " not found"));
    }
}
