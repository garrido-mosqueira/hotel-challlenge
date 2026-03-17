package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Room;
import com.fran.hotel.persistence.entity.RoomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomEntityMapper {

    RoomEntity toEntity(Room room);

    Room toDomain(RoomEntity roomEntity);

}
