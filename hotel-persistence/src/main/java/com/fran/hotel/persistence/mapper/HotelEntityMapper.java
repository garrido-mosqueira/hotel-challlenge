package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.persistence.entity.HotelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoomEntityMapper.class)
public interface HotelEntityMapper {

    @Mapping(target = "id", source = "hotelId")
    @Mapping(target = "city", source = "city") 
    @Mapping(target = "rooms", ignore = true) 
    HotelEntity toEntity(Hotel hotel);

    @Mapping(target = "hotelId", source = "id")
    @Mapping(target = "city", source = "city") 
    @Mapping(target = "address", constant = "")
    Hotel toDomain(HotelEntity hotelEntity);

}
