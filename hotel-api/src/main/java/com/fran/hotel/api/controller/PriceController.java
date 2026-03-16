package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.PriceResponse;
import com.fran.hotel.api.mapper.PriceDTOMapper;
import com.fran.hotel.domain.exception.NotFoundException;
import com.fran.hotel.domain.port.PriceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceUseCase useCase;
    private final PriceDTOMapper mapper;

    @GetMapping(value = "/", params = {"applicationDate", "productId", "brandId"})
    @ResponseStatus(HttpStatus.OK)
    public Mono<PriceResponse> getApplicablePrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId) {

        return useCase.getPrice(applicationDate, productId, brandId)
                .map(mapper::toResponse)
                .switchIfEmpty(Mono.error(new NotFoundException()));

    }

    @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Flux<PriceResponse> getAllPrices() {
        return useCase.getAllPrices()
                .onBackpressureDrop()
                .map(mapper::toResponse)
                .doOnError(error -> System.err.println("Error fetching prices: " + error.getMessage()))
                .onErrorResume(error -> Flux.empty());
    }

}
