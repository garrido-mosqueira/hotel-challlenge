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
    RoomEntity toEntity(Room room);

    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    Room toDomain(RoomEntity roomEntity);

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

}
