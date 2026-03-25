package com.fran.hotel.domain.exception;

public class InvalidReservationStateException extends ReservationException {
    public InvalidReservationStateException(String message) {
        super(message);
    }
}
