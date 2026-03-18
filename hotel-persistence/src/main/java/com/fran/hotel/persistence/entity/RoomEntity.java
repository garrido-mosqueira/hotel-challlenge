package com.fran.hotel.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "rooms")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String roomNumber;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    public RoomEntity() {}

    public RoomEntity(UUID id, String roomNumber, String type, HotelEntity hotel) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.hotel = hotel;
    }

}
