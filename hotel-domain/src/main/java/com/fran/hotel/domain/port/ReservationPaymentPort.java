package com.fran.hotel.domain.port;

import com.fran.hotel.domain.model.Reservation;

public interface ReservationPaymentPort {

    Reservation executeReservationPayment(Reservation reservation);

    void cancelReservationPayment(String reservationId);

}
