package com.fran.hotel.persistence.entity;

import com.fran.hotel.domain.model.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    private String id;

    private String guestId;
    private String roomId;
    private String rateId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public ReservationEntity() {}

    public ReservationEntity(String id, String guestId, String roomId, String rateId, LocalDate checkInDate, LocalDate checkOutDate, com.fran.hotel.domain.model.ReservationStatus status) {
        this.id = id;
        this.guestId = guestId;
        this.roomId = roomId;
        this.rateId = rateId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRateId() { return rateId; }
    public void setRateId(String rateId) { this.rateId = rateId; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public com.fran.hotel.domain.model.ReservationStatus getStatus() { return status; }
    public void setStatus(com.fran.hotel.domain.model.ReservationStatus status) { this.status = status; }
}
