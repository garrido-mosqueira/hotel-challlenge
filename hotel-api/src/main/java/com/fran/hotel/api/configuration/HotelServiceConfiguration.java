package com.fran.hotel.api.configuration;

import com.fran.hotel.application.service.HotelService;
import com.fran.hotel.application.service.ReservationService;
import com.fran.hotel.application.service.RoomService;
import com.fran.hotel.domain.port.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HotelServiceConfiguration {

    @Bean
    public HotelService hotelUseCase(HotelPersistencePort persistence) {
        return new HotelService(persistence);
    }

    @Bean
    public RoomService roomUseCase(RoomPersistencePort persistence) {
        return new RoomService(persistence);
    }

    @Bean
    public ReservationService reservationUseCase(ReservationPersistencePort persistence) {
        return new ReservationService(persistence);
    }

}
