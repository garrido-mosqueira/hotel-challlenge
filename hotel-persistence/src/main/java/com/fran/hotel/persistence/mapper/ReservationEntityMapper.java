package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.persistence.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationEntityMapper {

    ReservationEntity toEntity(Reservation reservation);

    @Mapping(target = "id", source = "id")
    Reservation toDomain(ReservationEntity reservationEntity);

}
