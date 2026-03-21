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
    @Column(columnDefinition = "uuid")
    private UUID id;
    private String roomNumber;
    private String type;
    private int floor;
    private String name;
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    public RoomEntity() {}

    public RoomEntity(UUID id, String roomNumber, String type, int floor, String name, boolean isAvailable, HotelEntity hotel) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.floor = floor;
        this.name = name;
        this.isAvailable = isAvailable;
        this.hotel = hotel;
    }

    public RoomEntity(UUID id, String roomNumber, String type, HotelEntity hotel) {
        this(id, roomNumber, type, 0, null, true, hotel);
    }

}
