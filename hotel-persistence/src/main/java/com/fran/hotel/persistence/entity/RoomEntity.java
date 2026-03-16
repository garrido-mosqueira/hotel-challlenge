package com.fran.hotel.persistence.entity;

import jakarta.persistence.*;

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public HotelEntity getHotel() { return hotel; }
    public void setHotel(HotelEntity hotel) { this.hotel = hotel; }
}
