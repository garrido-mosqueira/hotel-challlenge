package com.fran.hotel.api;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.entity.RoomEntity;
import com.fran.hotel.persistence.repository.HotelRepository;
import com.fran.hotel.persistence.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    private String baseUrl(String hotelId) { return "http://localhost:" + port + "/api/hotels/" + hotelId + "/rooms"; }

    @BeforeEach
    void setup() {
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        restClient = RestClient.builder().build();
    }

    @Test
    void getRooms() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);

        RoomEntity r1 = new RoomEntity(java.util.UUID.randomUUID(), "101", "STANDARD", h);
        RoomEntity r2 = new RoomEntity(java.util.UUID.randomUUID(), "102", "DELUXE", h);
        roomRepository.saveAll(List.of(r1, r2));

        ResponseEntity<List<RoomDto>> response = restClient.get()
                .uri(baseUrl(h.getId()))
                .retrieve()
                .toEntity(new org.springframework.core.ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getRoom() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        RoomEntity room = new RoomEntity(java.util.UUID.randomUUID(), "101", "STANDARD", h);
        room = roomRepository.save(room);

        ResponseEntity<RoomDto> response = restClient.get()
                .uri(baseUrl(h.getId()) + "/" + room.getId().toString())
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo("101");
    }

    @Test
    void getRoomNotFound() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);

        HotelEntity finalH = h;
        HttpClientErrorException exception = catchThrowableOfType(() ->
                restClient.get()
                        .uri(baseUrl(finalH.getId()) + "/" + java.util.UUID.randomUUID().toString())
                        .retrieve()
                        .toBodilessEntity(),
                HttpClientErrorException.class
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createRoomAutoGenerateId() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        hotelRepository.save(h);

        // Sending a room without an ID
        RoomDto request = new RoomDto(null, h.getId(), "STANDARD_TYPE", 1, "101", "Standard Room", true);

        ResponseEntity<RoomDto> response = restClient.post()
                .uri(baseUrl(h.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRoomId()).isNotNull(); // Expecting an auto-generated ID
        assertThat(response.getBody().getNumber()).isEqualTo("101");

        // Assuming it's generated as a proper UUID String now (from default domain model generation)
        String generatedId = response.getBody().getRoomId();
        UUID uuid;
        try {
           uuid = UUID.fromString(generatedId);
        } catch (IllegalArgumentException e) {
           uuid = new UUID(0L, Long.parseLong(generatedId));
        }

        RoomEntity saved = roomRepository.findById(uuid).orElse(null);
        
        assertThat(saved).isNotNull();
        assertThat(saved.getRoomNumber()).isEqualTo("101");
    }

    @Test
    void createRoom() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        hotelRepository.save(h);

        // Instead of pure long string like "123456", use a proper UUID format to avoid parse errors when fetching via ID mapping back to string or long logic
        String customId = UUID.randomUUID().toString();
        RoomDto request = new RoomDto(customId, h.getId(), "STANDARD_TYPE", 1, "101", "Standard Room", true);

        ResponseEntity<RoomDto> response = restClient.post()
                .uri(baseUrl(h.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo("101");

        RoomEntity saved = roomRepository.findById(UUID.fromString(customId)).orElse(null);
        assertThat(saved).as("Room should be saved in repository").isNotNull();
        assertThat(saved.getRoomNumber()).isEqualTo("101");
    }

    @Test
    void updateRoom() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        UUID roomIdUuid = UUID.randomUUID();
        RoomEntity room = new RoomEntity(roomIdUuid, "101", "STANDARD", h);
        room = roomRepository.save(room);

        String roomIdStr = roomIdUuid.toString();
        RoomDto request = new RoomDto(roomIdStr, h.getId(), "DELUXE_TYPE", 1, "102", "Deluxe Room", true);

        ResponseEntity<RoomDto> response = restClient.put()
                .uri(baseUrl(h.getId()) + "/" + roomIdStr)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo("102");

        RoomEntity updated = roomRepository.findById(roomIdUuid).orElseThrow();
        assertThat(updated.getRoomNumber()).isEqualTo("102");
    }

    @Test
    void deleteRoom() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        RoomEntity room = new RoomEntity(java.util.UUID.randomUUID(), "101", "STANDARD", h);
        room = roomRepository.save(room);

        String roomId = room.getId().toString();
        ResponseEntity<Void> response = restClient.delete()
                .uri(baseUrl(h.getId()) + "/" + roomId)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(roomRepository.findByHotelIdAndRoomId(h.getId(), room.getId())).isNull();
    }
}