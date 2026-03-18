package com.fran.hotel.domain.model;

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
