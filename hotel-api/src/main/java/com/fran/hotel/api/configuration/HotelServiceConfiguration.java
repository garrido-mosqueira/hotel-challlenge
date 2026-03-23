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
    public HotelUseCase hotelUseCase(HotelPersistencePort persistence) {
        return new HotelService(persistence);
    }

    @Bean
    public RoomUseCase roomUseCase(RoomPersistencePort persistence,
                                   RoomTypeInventoryPersistencePort roomTypeInventoryPersistencePort) {
        return new RoomService(persistence, roomTypeInventoryPersistencePort);
    }

    @Bean
    public ReservationUseCase reservationUseCase(ReservationPersistencePort persistence,
                                                 ReservationPaymentPort paymentPort,
                                                 RoomPersistencePort roomPersistencePort,
                                                 RoomTypeInventoryPersistencePort roomTypeInventoryPersistencePort) {
        return new ReservationService(persistence, paymentPort, roomPersistencePort, roomTypeInventoryPersistencePort);
    }

}
