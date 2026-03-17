package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.HotelDto;
import com.fran.hotel.api.mapper.HotelMapper;
import com.fran.hotel.domain.port.HotelUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelUseCase useCase;
    private final HotelMapper mapper;

    public HotelController(HotelUseCase useCase, HotelMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotel(@PathVariable String id) {
        var hotel = useCase.getHotel(id);
        if (hotel == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDto(hotel));
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> listHotels() {
        var hotels = useCase.getHotels();
        return ResponseEntity.ok(hotels.stream().map(mapper::toDto).toList());
    }

    @PostMapping
    public ResponseEntity<HotelDto> addHotel(@RequestBody HotelDto hotelDto) {
        var created = useCase.addHotel(mapper.toDomain(hotelDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable String id, @RequestBody HotelDto hotelDto) {
        var updated = useCase.updateHotel(mapper.toDomain(hotelDto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable String id) {
        useCase.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

}
