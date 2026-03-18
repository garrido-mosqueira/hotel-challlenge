package com.fran.hotel.domain.model;

public record ReservationTask(

    Reservation reservation,
    boolean isCancelled,
    int progress
) {
    public ReservationTask withProgress(int newProgress) {
        return new ReservationTask(reservation, isCancelled, newProgress);
    }
    
    public ReservationTask withReservation(Reservation newReservation) {
        return new ReservationTask(newReservation, isCancelled, progress);
    }
}
