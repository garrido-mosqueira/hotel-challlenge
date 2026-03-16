package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.ReservationDto;
import com.fran.hotel.domain.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationDTOMapper {

    ReservationDto toDto(Reservation reservation);

    Reservation toDomain(ReservationDto reservationDto);

}
