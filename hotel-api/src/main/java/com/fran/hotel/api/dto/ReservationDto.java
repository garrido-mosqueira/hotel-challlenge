package com.fran.hotel.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ReservationDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    private com.fran.hotel.domain.model.Guest guest;
    private com.fran.hotel.domain.model.Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private com.fran.hotel.domain.model.Rate rate;
    private com.fran.hotel.domain.model.ReservationStatus status;

    public ReservationDto() {}

    public ReservationDto(String id, com.fran.hotel.domain.model.Guest guest, com.fran.hotel.domain.model.Room room, LocalDate checkInDate, LocalDate checkOutDate, com.fran.hotel.domain.model.Rate rate, com.fran.hotel.domain.model.ReservationStatus status) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.rate = rate;
        this.status = status;
    }

}
