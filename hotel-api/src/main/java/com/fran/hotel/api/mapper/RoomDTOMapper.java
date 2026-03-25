package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.RoomDto;
import com.fran.hotel.domain.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomDTOMapper {

    RoomDto toDto(Room room);

    Room toDomain(RoomDto roomDto);

    @Mapping(target = "hotelId", source = "hotelId")
    @Mapping(target = "isAvailable", constant = "true")
    Room toDomainWithHotelId(RoomDto roomDto, String hotelId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hotelId", source = "hotelId")
    @Mapping(target = "isAvailable", constant = "true")
    Room toDomainWithIdAndHotelId(RoomDto roomDto, String id, String hotelId);
}
