package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<HotelEntity, String> {

    List<HotelEntity> findByCityContainingIgnoreCase(String city);

}