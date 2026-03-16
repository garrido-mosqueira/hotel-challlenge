package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<HotelEntity, String> {
}
