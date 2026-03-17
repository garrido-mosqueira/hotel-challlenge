package com.fran.hotel.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "hotels")
public class HotelEntity {

    @Id
    private String id;
    private String name;
    private String city;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoomEntity> rooms = new ArrayList<>();

    public HotelEntity() {
    }

    public HotelEntity(String id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

}
