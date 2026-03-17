package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.persistence.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomEntityMapper {

    @Mapping(target = "hotel", ignore = true)
    RoomEntity toEntity(Room room);

    Room toDomain(RoomEntity roomEntity);

}
