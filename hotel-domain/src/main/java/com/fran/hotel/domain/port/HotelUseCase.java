package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Hotel;

import java.util.List;

public interface HotelUseCase {

    Hotel getHotel(String id);

    List<Hotel> getHotels();

    Hotel addHotel(Hotel hotel);

    Hotel updateHotel(Hotel hotel);

    void deleteHotel(String id);

    List<Hotel> searchHotels(String city);

}