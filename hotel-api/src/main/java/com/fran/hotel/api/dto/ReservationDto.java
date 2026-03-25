package com.fran.hotel.api.dto;

import com.fran.hotel.domain.model.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ReservationDto {

    private String id;
    private String guestId;
    private String roomId;
    private String roomName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;

    public ReservationDto() {}

    public ReservationDto(String id, String guestId, String roomId, String roomName, LocalDate checkInDate, LocalDate checkOutDate, ReservationStatus status) {
        this.id = id;
        this.guestId = guestId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public ReservationDto(String id, String guestId, String roomId, LocalDate checkInDate, LocalDate checkOutDate, ReservationStatus status) {
        this(id, guestId, roomId, null, checkInDate, checkOutDate, status);
    }

}
