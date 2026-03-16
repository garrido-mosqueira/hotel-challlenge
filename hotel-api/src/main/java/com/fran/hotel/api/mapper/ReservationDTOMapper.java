package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.ReservationRequest;
import com.fran.hotel.api.dto.ReservationResponse;
import com.fran.hotel.domain.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationDTOMapper {

    ReservationResponse toResponse(Reservation reservation);

    Reservation toDomain(ReservationResponse reservationResponse);

    Reservation toDomain(ReservationRequest reservationRequest);

}
