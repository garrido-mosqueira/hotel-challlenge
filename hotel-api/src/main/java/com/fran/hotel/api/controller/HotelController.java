package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.HotelRequest;
import com.fran.hotel.api.dto.HotelResponse;
import com.fran.hotel.api.mapper.HotelMapper;
import com.fran.hotel.domain.port.HotelUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<HotelResponse> getHotel(@PathVariable String id) {
        var hotel = useCase.getHotel(id);
        if (hotel == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toResponse(hotel));
    }

    @PostMapping
    public ResponseEntity<HotelResponse> addHotel(@RequestBody HotelRequest hotelRequest) {
        var created = useCase.addHotel(mapper.toDomain(hotelRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> updateHotel(@PathVariable String id, @RequestBody HotelRequest hotelRequest) {
        var updated = useCase.updateHotel(mapper.toDomain(hotelRequest));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable String id) {
        useCase.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

}
