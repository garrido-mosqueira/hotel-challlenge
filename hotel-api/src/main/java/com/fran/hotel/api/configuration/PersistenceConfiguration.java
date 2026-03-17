package com.fran.hotel.api.configuration;

import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.domain.port.ReservationPersistencePort;
import com.fran.hotel.domain.port.RoomPersistencePort;
import com.fran.hotel.persistence.adapter.HotelPersistenceAdapter;
import com.fran.hotel.persistence.adapter.ReservationPersistenceAdapter;
import com.fran.hotel.persistence.adapter.RoomPersistenceAdapter;
import com.fran.hotel.persistence.mapper.HotelEntityMapper;
import com.fran.hotel.persistence.mapper.ReservationEntityMapper;
import com.fran.hotel.persistence.mapper.RoomEntityMapper;
import com.fran.hotel.persistence.repository.HotelRepository;
import com.fran.hotel.persistence.repository.ReservationRepository;
import com.fran.hotel.persistence.repository.RoomRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfiguration {

    @Bean
    public HotelPersistenceAdapter hotelPersistencePort(HotelRepository hotelRepository,
                                                        HotelEntityMapper hotelMapper) {
        return new HotelPersistenceAdapter(hotelRepository, hotelMapper);
    }

    @Bean
    public RoomPersistenceAdapter roomPersistencePort(RoomRepository roomRepository,
                                                      HotelRepository hotelRepository,
                                                      RoomEntityMapper roomMapper) {
        return new RoomPersistenceAdapter(roomRepository, hotelRepository, roomMapper);
    }

    @Bean
    public ReservationPersistenceAdapter reservationPersistencePort(ReservationRepository reservationRepository,
                                                                    ReservationEntityMapper reservationMapper) {
        return new ReservationPersistenceAdapter(reservationRepository, reservationMapper);
    }

}
