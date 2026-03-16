package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Hotel;

public interface HotelUseCase {
    Hotel getHotel(String id);

    Hotel addHotel(Hotel hotel);

    Hotel updateHotel(Hotel hotel);

    void deleteHotel(String id);
}
