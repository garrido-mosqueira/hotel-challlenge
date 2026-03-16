package com.fran.hotel.api.configuration;

import com.fran.hotel.application.service.PriceService;
import com.fran.hotel.domain.port.PricePersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceServiceConfiguration {

    @Bean
    public PriceService taskService(PricePersistencePort persistencePort) {
        return new PriceService(persistencePort);
    }

}
