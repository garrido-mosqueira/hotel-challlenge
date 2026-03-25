package com.fran.hotel.persistence.entity;

import com.fran.hotel.domain.model.RoomType;
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
    private String number;
    @Enumerated(EnumType.STRING)
    private RoomType typeId;
    private int floor;
    private String name;
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    public RoomEntity() {}

    public RoomEntity(UUID id, String number, RoomType typeId, int floor, String name, boolean isAvailable, HotelEntity hotel) {
        this.id = id;
        this.number = number;
        this.typeId = typeId;
        this.floor = floor;
        this.name = name;
        this.isAvailable = isAvailable;
        this.hotel = hotel;
    }

    public RoomEntity(UUID id, String number, RoomType typeId, HotelEntity hotel) {
        this(id, number, typeId, 0, null, true, hotel);
    }

}
