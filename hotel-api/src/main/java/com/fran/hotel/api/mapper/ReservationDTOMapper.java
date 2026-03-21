package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.ReservationDto;
import com.fran.hotel.domain.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationDTOMapper {

    ReservationDto toDto(Reservation reservation);

    @Mapping(target = "withStatus", ignore = true)
    Reservation toDomain(ReservationDto reservationDto);

}
