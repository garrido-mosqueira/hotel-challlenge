package com.fran.hotel.api;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.entity.RoomEntity;
import com.fran.hotel.persistence.repository.HotelRepository;
import com.fran.hotel.persistence.repository.RoomRepository;
import com.fran.hotel.persistence.repository.RoomTypeInventoryRepository;
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

import static com.fran.hotel.domain.model.RoomType.DOUBLE;
import static com.fran.hotel.domain.model.RoomType.SINGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeInventoryRepository roomTypeInventoryRepository;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    private String baseUrl(String hotelId) { return "http://localhost:" + port + "/api/hotels/" + hotelId + "/rooms"; }

    @BeforeEach
    void setup() {
        roomTypeInventoryRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        restClient = RestClient.builder().build();
    }

    @Test
    void getRooms() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);

        RoomEntity r1 = new RoomEntity(java.util.UUID.randomUUID(), "101", SINGLE, h);
        RoomEntity r2 = new RoomEntity(java.util.UUID.randomUUID(), "102", DOUBLE, h);

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
        
        RoomEntity room = new RoomEntity(java.util.UUID.randomUUID(), "101", SINGLE, h);
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
                        .uri(baseUrl(finalH.getId()) + "/" + UUID.randomUUID())
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
        RoomDto request = new RoomDto(null, h.getId(), SINGLE, 1, "101", "Standard Room", true);

        ResponseEntity<RoomDto> response = restClient.post()
                .uri(baseUrl(h.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull(); // Expecting an auto-generated ID
        assertThat(response.getBody().getNumber()).isEqualTo("101");

        // Assuming it's generated as a proper UUID String now (from default domain model generation)
        String generatedId = response.getBody().getId();
        UUID uuid;
        try {
           uuid = UUID.fromString(generatedId);
        } catch (IllegalArgumentException e) {
           uuid = new UUID(0L, Long.parseLong(generatedId));
        }

        RoomEntity saved = roomRepository.findById(uuid).orElse(null);
        
        assertThat(saved).isNotNull();
        assertThat(saved.getNumber()).isEqualTo("101");
    }

    @Test
    void createRoom() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        hotelRepository.save(h);

        // Instead of pure long string like "123456", use a proper UUID format to avoid parse errors when fetching via ID mapping back to string or long logic
        String customId = UUID.randomUUID().toString();
        RoomDto request = new RoomDto(customId, h.getId(), SINGLE, 1, "101", "Standard Room", true);

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
        assertThat(saved.getNumber()).isEqualTo("101");
    }

    @Test
    void createRoom_WhenHotelDoesNotExist_ShouldReturnNotFound() {
        // Given
        String nonExistentHotelId = "non-existent-hotel";
        RoomDto request = new RoomDto(null, nonExistentHotelId, SINGLE, 1, "101", "Standard Room", true);

        // When
        HttpClientErrorException exception = catchThrowableOfType(() ->
                        restClient.post()
                                .uri(baseUrl(nonExistentHotelId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity(),
                HttpClientErrorException.class
        );

        // Then
        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void updateRoom() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        UUID roomIdUuid = UUID.randomUUID();
        RoomEntity room = new RoomEntity(roomIdUuid, "101", SINGLE, h);
        room = roomRepository.save(room);

        String roomIdStr = roomIdUuid.toString();
        RoomDto request = new RoomDto(roomIdStr, h.getId(), DOUBLE, 1, "102", "Deluxe Room", true);

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
        assertThat(updated.getNumber()).isEqualTo("102");
    }

    @Test
    void updateRoomWithMismatchedIdInBody() {
        HotelEntity h = new HotelEntity("1", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);

        UUID roomIdUuid = UUID.randomUUID();
        RoomEntity room = new RoomEntity(roomIdUuid, "101", SINGLE, h);
        room = roomRepository.save(room);

        String roomIdStr = roomIdUuid.toString();
        // Body has a DIFFERENT ID and DIFFERENT hotel ID
        String differentIdStr = UUID.randomUUID().toString();
        RoomDto request = new RoomDto(differentIdStr, "DIFFERENT_HOTEL", DOUBLE, 1, "102", "Deluxe Room", true);

        ResponseEntity<RoomDto> response = restClient.put()
                .uri(baseUrl(h.getId()) + "/" + roomIdStr)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        // We expect the room in the path (roomIdStr) to be updated
        assertThat(response.getBody().getId()).isEqualTo(roomIdStr);
        assertThat(response.getBody().getHotelId()).isEqualTo(h.getId());
        assertThat(response.getBody().getNumber()).isEqualTo("102");

        RoomEntity updated = roomRepository.findById(roomIdUuid).orElseThrow();
        assertThat(updated.getNumber()).isEqualTo("102");
        assertThat(updated.getHotel().getId()).isEqualTo(h.getId());
        
        // Ensure no new room was created with differentIdStr
        assertThat(roomRepository.findById(UUID.fromString(differentIdStr))).isEmpty();
    }

    @Test
    void deleteRoom() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        RoomEntity room = new RoomEntity(java.util.UUID.randomUUID(), "101", SINGLE, h);
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