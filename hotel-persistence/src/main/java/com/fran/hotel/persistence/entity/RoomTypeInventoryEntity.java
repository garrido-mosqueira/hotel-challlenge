package com.fran.hotel.persistence.entity;

import com.fran.hotel.domain.model.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "room_type_inventory")
@Getter
@Setter
@NoArgsConstructor
public class RoomTypeInventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private HotelEntity hotel;

    @Enumerated(EnumType.STRING)
    private RoomType roomTypeId;
    private LocalDate date;
    private int totalInventory;
    private int totalReserved;

    public RoomTypeInventoryEntity(String id, HotelEntity hotel, RoomType roomTypeId, LocalDate date, int totalInventory, int totalReserved) {
        this.id = id;
        this.hotel = hotel;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.totalInventory = totalInventory;
        this.totalReserved = totalReserved;
    }
}
