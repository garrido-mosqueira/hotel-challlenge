package com.fran.hotel.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record ReservationPayment(

    Reservation reservation,
    boolean isCancelled,
    int progress

) {
}
