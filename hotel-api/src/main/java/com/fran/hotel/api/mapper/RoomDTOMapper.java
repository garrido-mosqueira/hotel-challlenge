package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.domain.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomDTOMapper {

    @Mapping(target = "roomId", source = "id")
    RoomDto toDto(Room room);

    @Mapping(target = "id", source = "roomId")
    Room toDomain(RoomDto roomDto);

}
