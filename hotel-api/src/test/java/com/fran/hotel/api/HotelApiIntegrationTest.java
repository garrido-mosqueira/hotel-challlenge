package com.fran.hotel.api;

import com.fran.hotel.api.dto.HotelDto;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.repository.HotelRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private HotelRepository hotelRepository;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    private String baseUrl() { return "http://localhost:" + port + "/api/hotels"; }

    @BeforeEach
    void setup() {
        hotelRepository.deleteAll();
        restClient = RestClient.builder().baseUrl(baseUrl()).build();
    }

    @Test
    void getHotel() {
        HotelEntity saved = hotelRepository.save(new HotelEntity("1", "Hotel One", "New York"));

        ResponseEntity<HotelDto> response = restClient.get()
                .uri("/" + saved.getId())
                .retrieve()
                .toEntity(HotelDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hotel One");
        assertThat(response.getBody().getCity()).isEqualTo("New York");
    }

    @Test
    void getHotelNotFound() {
        HttpClientErrorException exception = catchThrowableOfType(() ->
                restClient.get()
                        .uri("/unknown-id")
                        .retrieve()
                        .toBodilessEntity(),
                HttpClientErrorException.class
        );

        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void createHotel() {
        HotelDto request = new HotelDto("1", "Hotel One", "New York");

        ResponseEntity<HotelDto> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(HotelDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hotel One");

        HotelEntity saved = hotelRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Hotel One");
        assertThat(saved.getCity()).isEqualTo("New York");
    }

    @Test
    void createHotelWithoutId() {
        HotelDto request = new HotelDto(null, "Hotel Two", "Madrid");

        ResponseEntity<HotelDto> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(HotelDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void updateHotel() {
        HotelEntity saved = hotelRepository.save(new HotelEntity("1", "Hotel One", "New York"));

        HotelDto request = new HotelDto(saved.getId(), "Hotel Updated", "Miami");

        ResponseEntity<HotelDto> response = restClient.put()
                .uri("/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(HotelDto.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hotel Updated");
        assertThat(response.getBody().getCity()).isEqualTo("Miami");

        HotelEntity updated = hotelRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Hotel Updated");
        assertThat(updated.getCity()).isEqualTo("Miami");
    }

    @Test
    void deleteHotel() {
        HotelEntity saved = hotelRepository.save(new HotelEntity("1", "Hotel One", "New York"));

        ResponseEntity<Void> response = restClient.delete()
                .uri("/" + saved.getId())
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(hotelRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void listHotels() {
        hotelRepository.save(new HotelEntity("2", "Hotel 2", "Miami"));
        hotelRepository.save(new HotelEntity("3", "Hotel 3", "Miami"));

        ResponseEntity<List<HotelDto>> response = restClient.get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<HotelDto>>(){});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(HotelDto::getName).contains("Hotel 2", "Hotel 3");
    }

    @Test
    void searchHotels() {
        hotelRepository.save(new HotelEntity("4", "Beach Hotel", "Miami"));
        hotelRepository.save(new HotelEntity("5", "Mountain Resort", "Denver"));

        ResponseEntity<List<HotelDto>> response = restClient.get()
                .uri("/search?city=Miami")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<HotelDto>>(){});

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getName()).isEqualTo("Beach Hotel");
        assertThat(response.getBody().getFirst().getCity()).isEqualTo("Miami");
    }
}
