package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.persistence.entity.RoomTypeInventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RoomTypeInventoryEntityMapper {

    @Mapping(target = "hotel.id", source = "hotelId")
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    RoomTypeInventoryEntity toEntity(RoomTypeInventory domain);

    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    RoomTypeInventory toDomain(RoomTypeInventoryEntity entity);

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        if (id == null) return null;
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            try {
                return new UUID(0L, Long.parseLong(id));
            } catch (NumberFormatException nfe) {
                return UUID.nameUUIDFromBytes(id.getBytes());
            }
        }
    }

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        if (id == null) return null;
        if (id.getMostSignificantBits() == 0L) {
            return String.valueOf(id.getLeastSignificantBits());
        }
        return id.toString();
    }
}
