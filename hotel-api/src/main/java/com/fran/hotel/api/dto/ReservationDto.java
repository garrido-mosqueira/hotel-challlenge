package com.fran.hotel.api.dto;

import java.time.LocalDate;

public class ReservationDto {
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public com.fran.hotel.domain.model.Guest getGuest() { return guest; }
    public void setGuest(com.fran.hotel.domain.model.Guest guest) { this.guest = guest; }

    public com.fran.hotel.domain.model.Room getRoom() { return room; }
    public void setRoom(com.fran.hotel.domain.model.Room room) { this.room = room; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public com.fran.hotel.domain.model.Rate getRate() { return rate; }
    public void setRate(com.fran.hotel.domain.model.Rate rate) { this.rate = rate; }

    public com.fran.hotel.domain.model.ReservationStatus getStatus() { return status; }
    public void setStatus(com.fran.hotel.domain.model.ReservationStatus status) { this.status = status; }
}
