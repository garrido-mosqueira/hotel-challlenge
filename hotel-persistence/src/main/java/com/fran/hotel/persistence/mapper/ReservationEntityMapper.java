package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Reservation;
import com.fran.hotel.persistence.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class ReservationEntityMapper {

    public ReservationEntity toEntity(Reservation reservation) {
        if (reservation == null) return null;
        ReservationEntity entity = new ReservationEntity();
        if (reservation.id() != null) {
            try {
                entity.setId(UUID.fromString(reservation.id()));
            } catch (Exception e) {
                // Ignore invalid UUID
            }
        }
        entity.setGuestId(reservation.guestId());
        entity.setRoomId(reservation.roomId());
        entity.setRoomName(reservation.roomName());
        entity.setCheckInDate(reservation.checkInDate());
        entity.setCheckOutDate(reservation.checkOutDate());
        entity.setStatus(reservation.status());
        return entity;
    }

    @Mapping(target = "id", expression = "java(reservationEntity.getId() != null ? reservationEntity.getId().toString() : null)")
    @Mapping(target = "guestId", source = "guestId")
    @Mapping(target = "roomId", source = "roomId")
    @Mapping(target = "roomName", source = "roomName")
    public abstract Reservation toDomain(ReservationEntity reservationEntity);

}
