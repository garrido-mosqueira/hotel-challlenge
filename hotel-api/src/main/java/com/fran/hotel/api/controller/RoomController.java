package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.RoomRequest;
import com.fran.hotel.api.dto.RoomResponse;
import com.fran.hotel.api.mapper.RoomDTOMapper;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomUseCase useCase;
    private final RoomDTOMapper mapper;

    public RoomController(RoomUseCase useCase, RoomDTOMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable String hotelId, @PathVariable String roomId) {
        var room = useCase.getRoom(hotelId, roomId);
        if (room == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapper.toResponse(room));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> addRoom(@PathVariable String hotelId, @RequestBody RoomRequest request) {
        Room room = new Room(request.getId(), request.getRoomNumber(), request.getType());
        var created = useCase.addRoom(hotelId, room);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable String hotelId, @PathVariable String roomId, @RequestBody RoomRequest request) {
        Room room = new Room(roomId, request.getRoomNumber(), request.getType());
        var updated = useCase.updateRoom(hotelId, room);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String hotelId, @PathVariable String roomId) {
        useCase.deleteRoom(hotelId, roomId);
        return ResponseEntity.noContent().build();
    }

}
