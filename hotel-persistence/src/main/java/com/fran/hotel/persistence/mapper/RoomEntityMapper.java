package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.persistence.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RoomEntityMapper {

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "type", source = "roomTypeId")
    @Mapping(target = "roomNumber", source = "number")
    RoomEntity toEntity(Room room);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "roomTypeId", source = "type")
    @Mapping(target = "number", source = "roomNumber")
    @Mapping(target = "hotelId", source = "hotel.id")
    Room toDomain(RoomEntity roomEntity);

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        if (id == null) return null;
        try {
            // Check if it's already a valid UUID
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            // Generate a deterministic UUID from the String or treat it as a long value
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
        // Check if the most significant bits are 0. 
        // If so, it was likely generated from a Long as in our stringToUuid fallback.
        if (id.getMostSignificantBits() == 0L) {
            return String.valueOf(id.getLeastSignificantBits());
        }
        return id.toString();
    }

}
