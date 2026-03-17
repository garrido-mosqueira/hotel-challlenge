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
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    void createHotel() {
        HotelDto request = new HotelDto("h1", "Hotel One", "New York", List.of());

        ResponseEntity<HotelDto> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(HotelDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hotel One");

        HotelEntity saved = hotelRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Hotel One");
        assertThat(saved.getCity()).isEqualTo("New York");
    }

    @Test
    void listHotels() {
        hotelRepository.save(new HotelEntity("h2", "Hotel 2", "Miami"));
        hotelRepository.save(new HotelEntity("h3", "Hotel 3", "Miami"));

        ResponseEntity<List<HotelDto>> response = restClient.get()
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<HotelDto>>(){});

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(HotelDto::getName).contains("Hotel 2", "Hotel 3");
    }

    @Test
    void searchHotels() {
        hotelRepository.save(new HotelEntity("h4", "Beach Hotel", "Miami"));
        hotelRepository.save(new HotelEntity("h5", "Mountain Resort", "Denver"));

        ResponseEntity<List<HotelDto>> response = restClient.get()
                .uri("/search?city=Miami")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<HotelDto>>(){});

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Beach Hotel");
        assertThat(response.getBody().get(0).getCity()).isEqualTo("Miami");
    }
}