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
        entity.setGuestId(reservation.guest() != null ? reservation.guest().id() : null);
        entity.setRoomId(reservation.room() != null ? reservation.room().id() : null);
        entity.setRateId(reservation.rate() != null ? reservation.rate().id() : null);
        entity.setCheckInDate(reservation.checkInDate());
        entity.setCheckOutDate(reservation.checkOutDate());
        entity.setStatus(reservation.status());
        return entity;
    }

    @Mapping(target = "id", expression = "java(reservationEntity.getId() != null ? reservationEntity.getId().toString() : null)")
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
    public abstract Reservation toDomain(ReservationEntity reservationEntity);

}
