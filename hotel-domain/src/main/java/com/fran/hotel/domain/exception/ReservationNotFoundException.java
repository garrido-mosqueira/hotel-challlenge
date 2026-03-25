package com.fran.hotel.domain.exception;

public class ReservationNotFoundException extends ReservationException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
