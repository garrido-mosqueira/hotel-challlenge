package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.persistence.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomEntityMapper {

    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "available", source = "isAvailable")
    RoomEntity toEntity(Room room);

    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "isAvailable", source = "available")
    Room toDomain(RoomEntity roomEntity);

}
