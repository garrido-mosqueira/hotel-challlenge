package com.fran.hotel.domain.model;

public record ReservationPayment(

    Reservation reservation,
    boolean isCancelled,
    int progress

) {
}
