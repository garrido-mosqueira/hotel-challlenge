package com.fran.hotel.persistence.adapter;

import com.fran.expedia.domain.model.Price;
import com.fran.expedia.domain.port.PricePersistencePort;
import com.fran.hotel.persistence.mapper.PricePersistenceMapper;
import com.fran.hotel.persistence.repository.PriceJpaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class PricePersistenceAdapter implements PricePersistencePort {

    private final PriceJpaRepository repository;
    private final PricePersistenceMapper mapper;

    public PricePersistenceAdapter(PriceJpaRepository repository, PricePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Price> getPrice(LocalDateTime applicationDate, Long productId, Long brandId) {
        return repository.findTopByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        brandId, productId, applicationDate)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Price> getAllPrices() {
        return repository.findAll()
                .map(mapper::toDomain);
    }

}
