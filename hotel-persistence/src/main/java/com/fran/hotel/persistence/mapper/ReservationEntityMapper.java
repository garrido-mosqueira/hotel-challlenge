package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.persistence.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationEntityMapper {

    @Mapping(target = "guestId", source = "guest.id")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "rateId", source = "rate.id")
    ReservationEntity toEntity(Reservation reservation);

    @Mapping(target = "guest.id", source = "guestId")
    @Mapping(target = "guest.firstName", constant = "")
    @Mapping(target = "guest.lastName", constant = "")
    @Mapping(target = "guest.email", constant = "")
    @Mapping(target = "room.id", source = "roomId")
    @Mapping(target = "room.roomNumber", constant = "")
    @Mapping(target = "room.type", constant = "")
    @Mapping(target = "rate.id", source = "rateId")
    @Mapping(target = "rate.name", constant = "")
    @Mapping(target = "rate.amount", ignore = true)
    Reservation toDomain(ReservationEntity reservationEntity);

}
