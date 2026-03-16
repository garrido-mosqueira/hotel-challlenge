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
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomApiIntegrationTest extends TestContainerConfiguration {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl(String hotelId) { return "http://localhost:" + port + "/api/hotels/" + hotelId + "/rooms"; }

    @BeforeEach
    void setup() {
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
    }

    @Test
    void createRoom() {
        HotelEntity h = new HotelEntity("h10", "Hotel 10");
        hotelRepository.save(h);

        RoomDto request = new RoomDto("r1", "101", "STANDARD");

        ResponseEntity<RoomDto> response = restTemplate.postForEntity(baseUrl(h.getId()), request, RoomDto.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRoomNumber()).isEqualTo("101");

        RoomEntity saved = roomRepository.findByHotelIdAndRoomId(h.getId(), response.getBody().getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getRoomNumber()).isEqualTo("101");
    }
}
