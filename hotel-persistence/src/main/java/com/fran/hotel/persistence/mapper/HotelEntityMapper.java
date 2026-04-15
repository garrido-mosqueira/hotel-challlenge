package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.persistence.entity.HotelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoomEntityMapper.class)
public interface HotelEntityMapper {

    @Mapping(target = "rooms", ignore = true) 
    HotelEntity toEntity(Hotel hotel);

    Hotel toDomain(HotelEntity hotelEntity);

}
