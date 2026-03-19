package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.api.mapper.RoomDTOMapper;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomUseCase useCase;
    private final RoomDTOMapper mapper;

    public RoomController(RoomUseCase useCase, RoomDTOMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getRooms(@PathVariable String hotelId) {
        var rooms = useCase.getRooms(hotelId);
        return ResponseEntity.ok(rooms.stream().map(mapper::toDto).toList());
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable String hotelId, @PathVariable String roomId) {
        var room = useCase.getRoom(hotelId, roomId);
        if (room == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toDto(room));
    }

    @PostMapping
    public ResponseEntity<RoomDto> addRoom(@PathVariable String hotelId, @RequestBody RoomDto request) {
        var created = useCase.addRoom(hotelId, mapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable String hotelId, @PathVariable String roomId, @RequestBody RoomDto request) {
        Room room = mapper.toDomain(request);
        var updated = useCase.updateRoom(hotelId, room);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String hotelId, @PathVariable String roomId) {
        useCase.deleteRoom(hotelId, roomId);
        return ResponseEntity.noContent().build();
    }

}
