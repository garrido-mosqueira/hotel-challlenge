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
    @Mapping(target = "id", source = "roomId", qualifiedByName = "longToUuid")
    @Mapping(target = "type", source = "roomTypeId")
    @Mapping(target = "roomNumber", source = "number")
    RoomEntity toEntity(Room room);

    @Mapping(target = "roomId", source = "id", qualifiedByName = "uuidToLong")
    @Mapping(target = "roomTypeId", source = "type")
    @Mapping(target = "number", source = "roomNumber")
    // Map missing fields if needed, ignoring for now as they might not be in entity
    @Mapping(target = "hotelId", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    Room toDomain(RoomEntity roomEntity);

    @Named("longToUuid")
    default UUID longToUuid(Long id) {
        if (id == null) return null;
        return new UUID(0L, id);
    }

    @Named("uuidToLong")
    default Long uuidToLong(UUID id) {
        return id != null ? id.getLeastSignificantBits() : null;
    }

}
