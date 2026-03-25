package com.fran.hotel.api;

import com.fran.hotel.api.dto.ReservationDto;
import com.fran.hotel.domain.model.Guest;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.model.RoomType;
import com.fran.hotel.persistence.entity.HotelEntity;
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

    @Autowired
    private com.fran.hotel.persistence.repository.RoomTypeInventoryRepository roomTypeInventoryRepository;

    @Autowired
    private com.fran.hotel.persistence.repository.HotelRepository hotelRepository;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    private final Guest defaultGuest = new Guest(1L, "John", "Doe", "john.doe@example.com");
    private final String roomIdStr = UUID.randomUUID().toString();

    private String baseUrl() { return "http://localhost:" + port + "/api/reservations"; }

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
        roomTypeInventoryRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();

        HotelEntity hotel = new HotelEntity("1", "Test Hotel", "Madrid");
        hotelRepository.save(hotel);

        RoomEntity room = new RoomEntity(UUID.randomUUID().toString(), "101", RoomType.SINGLE, 1, "Standard Room", true, hotel);
        roomRepository.save(room);

        LocalDate now = LocalDate.now();
        roomTypeInventoryRepository.save(new com.fran.hotel.persistence.entity.RoomTypeInventoryEntity(null, hotel, RoomType.SINGLE, now, 10, 0));
        roomTypeInventoryRepository.save(new com.fran.hotel.persistence.entity.RoomTypeInventoryEntity(null, hotel, RoomType.SINGLE, now.plusDays(1), 10, 0));
        roomTypeInventoryRepository.save(new com.fran.hotel.persistence.entity.RoomTypeInventoryEntity(null, hotel, RoomType.SINGLE, now.plusDays(2), 10, 0));
        roomTypeInventoryRepository.save(new com.fran.hotel.persistence.entity.RoomTypeInventoryEntity(null, hotel, RoomType.SINGLE, now.plusDays(3), 10, 0));

        restClient = RestClient.builder().baseUrl(baseUrl()).build();
    }

    @Test
    void getReservationsForUser() {
        ReservationEntity res1 = new ReservationEntity(null, String.valueOf(defaultGuest.guestId()), roomIdStr, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        ReservationEntity res2 = new ReservationEntity(null, "user-2", UUID.randomUUID().toString(), LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        reservationRepository.saveAll(List.of(res1, res2));
        reservationRepository.flush();

        ResponseEntity<List<ReservationDto>> response = restClient.get()
                .header("X-User-Id", String.valueOf(defaultGuest.guestId()))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(res1.getId());
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
        ReservationEntity res1 = new ReservationEntity(null, String.valueOf(defaultGuest.guestId()), roomIdStr, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        reservationRepository.save(res1);
        reservationRepository.flush();

        ResponseEntity<ReservationDto> response = restClient.get()
                .uri("/" + res1.getId())
                .retrieve()
                .toEntity(ReservationDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(res1.getId());
        assertThat(response.getBody().getGuestId()).isEqualTo(String.valueOf(defaultGuest.guestId()));
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
        RoomEntity room = roomRepository.findAll().getFirst();
        ReservationDto request = new ReservationDto("provided-id", String.valueOf(defaultGuest.guestId()), room.getId(), LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);

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

        ReservationEntity saved = reservationRepository.findById(createdId).orElseThrow();
        assertThat(saved.getGuestId()).isEqualTo(String.valueOf(defaultGuest.guestId()));
        assertThat(saved.getRoomId()).isEqualTo(room.getId());
    }

    @Test
    void createReservationShouldFailWhenOverlapping() {
        RoomEntity room = roomRepository.findAll().getFirst();
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);
        
        // Create an existing reservation
        ReservationEntity existing = new ReservationEntity(null, "guest-2", room.getId(), checkIn, checkOut, ReservationStatus.PENDING);
        reservationRepository.save(existing);
        reservationRepository.flush();

        // Try to create an overlapping reservation
        ReservationDto request = new ReservationDto(null, String.valueOf(defaultGuest.guestId()), room.getId(), checkIn.plusDays(1), checkOut.plusDays(1), ReservationStatus.PENDING);

        HttpClientErrorException exception = catchThrowableOfType(() ->
            restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity(),
            HttpClientErrorException.class
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(409);
        assertThat(exception.getResponseBodyAsString()).contains("Room is already reserved for the selected dates");
    }

    @Test
    void cancelReservation() {
        ReservationEntity res1 = new ReservationEntity(null, String.valueOf(defaultGuest.guestId()), roomIdStr, LocalDate.now(), LocalDate.now().plusDays(2), ReservationStatus.PENDING);
        reservationRepository.save(res1);
        reservationRepository.flush();

        ResponseEntity<Void> response = restClient.delete()
                .uri("/" + res1.getId())
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        
        ReservationEntity deleted = reservationRepository.findById(res1.getId()).orElseThrow();
        assertThat(deleted.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

}