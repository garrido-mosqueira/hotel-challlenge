package com.fran.hotel.api.dto;

import com.fran.hotel.domain.model.Guest;
import com.fran.hotel.domain.model.Rate;
import com.fran.hotel.domain.model.ReservationStatus;
import com.fran.hotel.domain.model.Room;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ReservationDto {

    private String id;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Rate rate;
    private ReservationStatus status;

    public ReservationDto() {}

    public ReservationDto(String id, Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate, Rate rate, ReservationStatus status) {
        this.id = id;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.rate = rate;
        this.status = status;
    }

}
