package com.fran.hotel.api;

import com.fran.hotel.api.dto.ReservationDto;
import com.fran.hotel.persistence.entity.ReservationEntity;
import com.fran.hotel.persistence.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl() { return "http://localhost:" + port + "/api/reservations"; }

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
    }

    @Test
    void createReservation() {
        ReservationDto request = new ReservationDto("res1", null, null, LocalDate.now(), LocalDate.now().plusDays(2), null, com.fran.hotel.domain.model.ReservationStatus.PENDING);

        ResponseEntity<ReservationDto> response = restTemplate.postForEntity(baseUrl(), request, ReservationDto.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();

        ReservationEntity saved = reservationRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(saved.getGuestId()).isEqualTo("null").isNotNull();
    }
}
