package com.fran.hotel.api.configuration;

import com.fran.hotel.persistence.adapter.HotelPersistenceAdapter;
import com.fran.hotel.persistence.adapter.ReservationPersistenceAdapter;
import com.fran.hotel.persistence.adapter.RoomPersistenceAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    HotelPersistenceAdapter.class,
    RoomPersistenceAdapter.class,
    ReservationPersistenceAdapter.class
})
public class PersistenceConfiguration {
}
