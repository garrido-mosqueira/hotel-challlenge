package com.fran.hotel.api.dto;

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
    private com.fran.hotel.domain.model.Guest guest;
    private com.fran.hotel.domain.model.Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private com.fran.hotel.domain.model.Rate rate;
    private com.fran.hotel.domain.model.ReservationStatus status;

}
