package com.fran.hotel.persistence.entity;

import com.fran.hotel.domain.model.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String guestId;
    private String roomId;
    private String rateId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public ReservationEntity() {}

    public ReservationEntity(UUID id, String guestId, String roomId, String rateId, LocalDate checkInDate, LocalDate checkOutDate, com.fran.hotel.domain.model.ReservationStatus status) {
        this.id = id;
        this.guestId = guestId;
        this.roomId = roomId;
        this.rateId = rateId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

}
