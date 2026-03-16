package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.ReservationRequest;
import com.fran.hotel.api.dto.ReservationResponse;
import com.fran.hotel.api.mapper.ReservationDTOMapper;
import com.fran.hotel.domain.model.*;
import com.fran.hotel.domain.port.ReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationUseCase useCase;
    private final ReservationDTOMapper mapper;

    @GetMapping
    public List<ReservationResponse> getReservations(@RequestHeader(value = "X-User-Id", required = false) String userId) {
        // for demo, if no header provided, return empty
        if (userId == null) return List.of();
        return useCase.getReservationsForUser(userId).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String id) {
        var reservation = useCase.getReservation(id);
        if (reservation == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toResponse(reservation));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest reservationRequest) {
        // build minimal Reservation using domain models (guest, room, rate lookups are out of scope)
        Guest guest = new Guest(reservationRequest.getId(), "", "", "");
        Room room = new Room(reservationRequest.getRoomId(), "", "");
        Rate rate = new Rate(reservationRequest.getRateId(), "", null);
        Reservation reservation = new Reservation(reservationRequest.getId(), guest, room, reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate(), rate, ReservationStatus.PENDING);
        var created = useCase.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        useCase.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

}
