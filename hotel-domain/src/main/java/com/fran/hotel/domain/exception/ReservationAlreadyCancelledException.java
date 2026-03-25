package com.fran.hotel.domain.exception;

public class ReservationAlreadyCancelledException extends InvalidReservationStateException {
    public ReservationAlreadyCancelledException(String message) {
        super(message);
    }
}
