package com.fran.hotel.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record Guest(
    Long guestId,
    String firstName,
    String lastName,
    String email
) {
    // Records allow compact constructors for validation
    public Guest {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
