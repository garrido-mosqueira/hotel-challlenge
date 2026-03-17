package com.fran.hotel.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "rooms")
public class RoomEntity {
    @Id
    private String id;
    private String roomNumber;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    public RoomEntity() {}

    public RoomEntity(String id, String roomNumber, String type, HotelEntity hotel) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.hotel = hotel;
    }

}
