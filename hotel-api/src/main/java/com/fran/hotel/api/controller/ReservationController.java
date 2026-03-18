package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.ReservationDto;
import com.fran.hotel.api.mapper.ReservationDTOMapper;
import com.fran.hotel.domain.port.ReservationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationUseCase useCase;
    private final ReservationDTOMapper mapper;

    public ReservationController(ReservationUseCase useCase, ReservationDTOMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ReservationDto> getReservations(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) return List.of();
        return useCase.getReservationsForUser(userId).stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservation(@PathVariable String id) {
        var reservation = useCase.getReservation(id);
        if (reservation == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDto(reservation));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto) {
        var created = useCase.createReservation(mapper.toDomain(reservationDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        useCase.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

}
