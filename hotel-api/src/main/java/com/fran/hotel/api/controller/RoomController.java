package com.fran.hotel.api.controller;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.api.mapper.RoomDTOMapper;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.domain.port.RoomUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
        return ResponseEntity.ok(mapper.toDto(room));
    }

    @PostMapping
    public ResponseEntity<RoomDto> addRoom(@PathVariable String hotelId, @RequestBody RoomDto request) {
        // Inject the hotelId during the mapping phase so the Domain object is complete
        Room roomToCreate = mapper.toDomainWithHotelId(request, hotelId);
        var created = useCase.addRoom(roomToCreate);
        
        // Provide the URI of the newly created resource
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{roomId}")
                .buildAndExpand(created.id())
                .toUri();
                
        return ResponseEntity.created(location).body(mapper.toDto(created));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable String hotelId, @PathVariable String roomId, @RequestBody RoomDto request) {
        Room room = mapper.toDomainWithIdAndHotelId(request, roomId, hotelId);
        var updated = useCase.updateRoom(room);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String hotelId, @PathVariable String roomId) {
        useCase.deleteRoom(hotelId, roomId);
        return ResponseEntity.noContent().build();
    }

}
