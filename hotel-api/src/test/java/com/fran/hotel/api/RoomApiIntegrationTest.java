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

        RoomEntity r1 = new RoomEntity(null, "101", "STANDARD", h);
        RoomEntity r2 = new RoomEntity(null, "102", "DELUXE", h);
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
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        RoomEntity room = new RoomEntity(null, "101", "STANDARD", h);
        room = roomRepository.save(room);

        ResponseEntity<RoomDto> response = restClient.get()
                .uri(baseUrl(h.getId()) + "/" + room.getId().toString())
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRoomNumber()).isEqualTo("101");
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
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        hotelRepository.save(h);

        // Sending a room without an ID
        RoomDto request = new RoomDto(null, "101", "STANDARD");

        ResponseEntity<RoomDto> response = restClient.post()
                .uri(baseUrl(h.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull(); // Expecting an auto-generated ID
        assertThat(response.getBody().getRoomNumber()).isEqualTo("101");

        RoomEntity saved = roomRepository.findByHotelIdAndRoomId(h.getId(), java.util.UUID.fromString(response.getBody().getId()));
        assertThat(saved).isNotNull();
        assertThat(saved.getRoomNumber()).isEqualTo("101");
    }

    @Test
    void createRoom() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        hotelRepository.save(h);

        String customId = java.util.UUID.randomUUID().toString();
        RoomDto request = new RoomDto(customId, "101", "STANDARD");

        ResponseEntity<RoomDto> response = restClient.post()
                .uri(baseUrl(h.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRoomNumber()).isEqualTo("101");

        RoomEntity saved = roomRepository.findByHotelIdAndRoomId(h.getId(), java.util.UUID.fromString(response.getBody().getId()));
        assertThat(saved).isNotNull();
        assertThat(saved.getRoomNumber()).isEqualTo("101");
    }

    @Test
    void updateRoom() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        RoomEntity room = new RoomEntity(null, "101", "STANDARD", h);
        room = roomRepository.save(room);

        String roomId = room.getId().toString();
        RoomDto request = new RoomDto(roomId, "102", "DELUXE");

        ResponseEntity<RoomDto> response = restClient.put()
                .uri(baseUrl(h.getId()) + "/" + roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(RoomDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRoomNumber()).isEqualTo("102");
        assertThat(response.getBody().getType()).isEqualTo("DELUXE");

        RoomEntity updated = roomRepository.findByHotelIdAndRoomId(h.getId(), room.getId());
        assertThat(updated).isNotNull();
        assertThat(updated.getRoomNumber()).isEqualTo("102");
    }

    @Test
    void deleteRoom() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10", "Madrid");
        h = hotelRepository.save(h);
        
        RoomEntity room = new RoomEntity(null, "101", "STANDARD", h);
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