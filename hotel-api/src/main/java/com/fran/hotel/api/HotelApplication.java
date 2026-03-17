package com.fran.hotel.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.fran.hotel")
@EnableCaching
@EnableJpaRepositories(basePackages = "com.fran.hotel.persistence.repository")
@EntityScan(basePackages = "com.fran.hotel.persistence.entity")
public class HotelApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }
}
