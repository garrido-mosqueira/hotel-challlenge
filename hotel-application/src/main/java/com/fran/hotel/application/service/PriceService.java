package com.fran.hotel.application.service;

import com.fran.hotel.domain.model.Price;
import com.fran.hotel.domain.port.PricePersistencePort;
import com.fran.hotel.domain.port.PriceUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class PriceService implements PriceUseCase {

    private final PricePersistencePort pricePersistencePort;

    public PriceService(PricePersistencePort pricePersistencePort) {
        this.pricePersistencePort = pricePersistencePort;
    }

    @Override
    public Mono<Price> getPrice(LocalDateTime date, Long productId, Long brandId) {
        return pricePersistencePort.getPrice(date, productId, brandId);
    }

    @Override
    public Flux<Price> getAllPrices() {
        return pricePersistencePort.getAllPrices();
    }

}
