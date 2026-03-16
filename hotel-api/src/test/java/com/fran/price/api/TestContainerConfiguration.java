package com.fran.price.api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class TestContainerConfiguration {

    @DynamicPropertySource
    static void setH2Properties(final DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///pricesdb;DB_CLOSE_DELAY=-1");
        registry.add("spring.r2dbc.username", () -> "sa");
        registry.add("spring.r2dbc.password", () -> "sa");
        registry.add("spring.sql.init.mode", () -> "always");
    }
}
