package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.RoomRequest;
import com.fran.hotel.api.dto.RoomResponse;
import com.fran.hotel.domain.model.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomDTOMapper {

    RoomResponse toResponse(Room room);

    Room toDomain(RoomResponse roomResponse);

    Room toDomain(RoomRequest roomRequest);

}
