package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.persistence.entity.HotelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoomEntityMapper.class)
public interface HotelEntityMapper {

    @Mapping(target = "id", expression = "java(hotel.hotelId() != null ? String.valueOf(hotel.hotelId()) : null)")
    @Mapping(target = "city", source = "city") 
    @Mapping(target = "rooms", ignore = true) 
    HotelEntity toEntity(Hotel hotel);

    @Mapping(target = "hotelId", expression = "java(hotelEntity.getId() != null ? Long.valueOf(hotelEntity.getId()) : null)")
    @Mapping(target = "city", source = "city") 
    @Mapping(target = "address", constant = "")
    Hotel toDomain(HotelEntity hotelEntity);

}
