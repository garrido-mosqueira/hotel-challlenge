package com.fran.price.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled
class HotelApplicationIntegrationTest extends TestContainerConfiguration {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    // Test 1: petición a las 10:00 del día 14 del producto 35455 para la brand 1
    @Test
    void test1_petition_14_a_las_10() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prices/")
                        .queryParam("applicationDate", "2020-06-14T10:00:00")
                        .queryParam("productId", 35455)
                        .queryParam("brandId", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(35455)
                .jsonPath("$.brandId").isEqualTo(1)
                .jsonPath("$.priceList").isEqualTo(1)
                .jsonPath("$.price").isEqualTo(35.50);
    }

    // Test 2: petición a las 16:00 del día 14 del producto 35455 para la brand 1
    @Test
    void test2_petition_14_a_las_16() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prices/")
                        .queryParam("applicationDate", "2020-06-14T16:00:00")
                        .queryParam("productId", 35455)
                        .queryParam("brandId", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(35455)
                .jsonPath("$.brandId").isEqualTo(1)
                .jsonPath("$.priceList").isEqualTo(2)
                .jsonPath("$.price").isEqualTo(25.45);
    }

    // Test 3: petición a las 21:00 del día 14 del producto 35455 para la brand 1
    @Test
    void test3_petition_14_a_las_21() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prices/")
                        .queryParam("applicationDate", "2020-06-14T21:00:00")
                        .queryParam("productId", 35455)
                        .queryParam("brandId", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(35455)
                .jsonPath("$.brandId").isEqualTo(1)
                .jsonPath("$.priceList").isEqualTo(1)
                .jsonPath("$.price").isEqualTo(35.50);
    }

    // Test 4: petición a las 10:00 del día 15 del producto 35455 para la brand 1
    @Test
    void test4_petition_15_a_las_10() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prices/")
                        .queryParam("applicationDate", "2020-06-15T10:00:00")
                        .queryParam("productId", 35455)
                        .queryParam("brandId", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(35455)
                .jsonPath("$.brandId").isEqualTo(1)
                .jsonPath("$.priceList").isEqualTo(3)
                .jsonPath("$.price").isEqualTo(30.50);
    }

    // Test 5: petición a las 21:00 del día 16 del producto 35455 para la brand 1
    @Test
    void test5_petition_16_a_las_21() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/prices/")
                        .queryParam("applicationDate", "2020-06-16T21:00:00")
                        .queryParam("productId", 35455)
                        .queryParam("brandId", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(35455)
                .jsonPath("$.brandId").isEqualTo(1)
                .jsonPath("$.priceList").isEqualTo(4)
                .jsonPath("$.price").isEqualTo(38.95);
    }

}
