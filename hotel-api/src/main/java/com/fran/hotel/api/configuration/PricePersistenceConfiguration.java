package com.fran.hotel.api.configuration;

import com.fran.hotel.persistence.adapter.PricePersistenceAdapter;
import com.fran.hotel.persistence.mapper.PricePersistenceMapper;
import com.fran.hotel.persistence.repository.PriceJpaRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PricePersistenceConfiguration {

    @Bean
    public PricePersistenceMapper pricePersistenceMapper() {
        return Mappers.getMapper(PricePersistenceMapper.class);
    }

    @Bean
    public PricePersistenceAdapter pricePersistenceAdapter(PriceJpaRepository repository) {
        return new PricePersistenceAdapter(repository, pricePersistenceMapper());
    }


}
