package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.domain.model.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomDTOMapper {

    RoomDto toDto(Room room);

    Room toDomain(RoomDto roomDto);

}
