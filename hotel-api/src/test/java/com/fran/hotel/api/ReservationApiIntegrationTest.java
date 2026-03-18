package com.fran.hotel.api;

import com.fran.hotel.api.dto.ReservationDto;
import com.fran.hotel.domain.model.Guest;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.model.Room;
import com.fran.hotel.persistence.entity.ReservationEntity;
import com.fran.hotel.persistence.entity.RoomEntity;
import com.fran.hotel.persistence.repository.ReservationRepository;
import com.fran.hotel.persistence.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    private final Guest defaultGuest = new Guest("user-1", "John", "Doe", "john.doe@example.com");
    private final String roomId = UUID.randomUUID().toString();
    private final Room defaultRoom = new Room(roomId, "101", "Standard");

    private String baseUrl() { return "http://localhost:" + port + "/api/reservations"; }

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
        roomRepository.save(new RoomEntity(UUID.fromString(defaultRoom.id()), defaultRoom.roomNumber(), defaultRoom.type(), null));
        restClient = RestClient.builder().baseUrl(baseUrl()).build();
    }

    @Test
    void getReservationsForUser() {
        ReservationEntity res1 = new ReservationEntity(null, defaultGuest.id(), defaultRoom.id(), null, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        ReservationEntity res2 = new ReservationEntity(null, "user-2", UUID.randomUUID().toString(), null, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        reservationRepository.saveAll(List.of(res1, res2));
        reservationRepository.flush();

        ResponseEntity<List<ReservationDto>> response = restClient.get()
                .header("X-User-Id", defaultGuest.id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(res1.getId().toString());
    }

    @Test
    void getReservationsForUserMissingHeader() {
        ResponseEntity<List<ReservationDto>> response = restClient.get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getReservation() {
        ReservationEntity res1 = new ReservationEntity(null, defaultGuest.id(), defaultRoom.id(), null, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        reservationRepository.save(res1);
        reservationRepository.flush();

        ResponseEntity<ReservationDto> response = restClient.get()
                .uri("/" + res1.getId())
                .retrieve()
                .toEntity(ReservationDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(res1.getId().toString());
        assertThat(response.getBody().getGuest().id()).isEqualTo(defaultGuest.id());
    }

    @Test
    void getReservationNotFound() {
        HttpClientErrorException exception = catchThrowableOfType(() -> 
            restClient.get()
                .uri("/" + UUID.randomUUID())
                .retrieve()
                .toBodilessEntity(), 
            HttpClientErrorException.class
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createReservation() {
        ReservationDto request = new ReservationDto("provided-id", defaultGuest, defaultRoom, LocalDate.now(), LocalDate.now().plusDays(2), null, ReservationStatus.PENDING);

        ResponseEntity<ReservationDto> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(ReservationDto.class);
                
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        
        String createdId = response.getBody().getId();
        assertThat(createdId).isNotNull();
        assertThat(createdId).isNotEqualTo("provided-id");

        ReservationEntity saved = reservationRepository.findById(UUID.fromString(createdId)).orElseThrow();
        assertThat(saved.getGuestId()).isEqualTo(defaultGuest.id());
        assertThat(saved.getRoomId()).isEqualTo(defaultRoom.id());
    }

    @Test
    void cancelReservation() {
        ReservationEntity res1 = new ReservationEntity(null, defaultGuest.id(), defaultRoom.id(), null, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        reservationRepository.save(res1);
        reservationRepository.flush();

        ResponseEntity<Void> response = restClient.delete()
                .uri("/" + res1.getId())
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        
        ReservationEntity deleted = reservationRepository.findById(res1.getId()).orElseThrow();
        assertThat(deleted.getStatus()).isEqualTo(com.fran.hotel.domain.model.ReservationStatus.CANCELED);
    }

}