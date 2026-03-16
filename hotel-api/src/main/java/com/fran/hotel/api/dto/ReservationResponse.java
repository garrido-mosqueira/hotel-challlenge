package com.fran.hotel.api.dto;

import com.fran.hotel.domain.model.Guest;
import com.fran.hotel.domain.model.Rate;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {

    private String id;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Rate rate;
    private ReservationStatus status;

}
