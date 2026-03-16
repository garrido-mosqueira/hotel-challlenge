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
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl() { return "http://localhost:" + port + "/api/hotels"; }

    @BeforeEach
    void setup() {
        hotelRepository.deleteAll();
    }

    @Test
    void createHotel() {
        HotelDto request = new HotelDto("h1", "Hotel One", List.of());

        ResponseEntity<HotelDto> response = restTemplate.postForEntity(baseUrl(), request, HotelDto.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hotel One");

        HotelEntity saved = hotelRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Hotel One");
    }

    @Test
    void listHotels() {
        hotelRepository.save(new HotelEntity("h2", "Hotel 2"));
        hotelRepository.save(new HotelEntity("h3", "Hotel 3"));

        ResponseEntity<List<HotelDto>> response = restTemplate.exchange(baseUrl(), org.springframework.http.HttpMethod.GET, null, new ParameterizedTypeReference<List<HotelDto>>(){});
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).extracting(HotelDto::getName).contains("Hotel 2", "Hotel 3");
    }
}
