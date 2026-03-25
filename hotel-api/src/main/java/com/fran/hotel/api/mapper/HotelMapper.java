package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.HotelDto;
import com.fran.hotel.domain.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    HotelDto toDto(Hotel hotel);

    Hotel toDomain(HotelDto hotelDto);

    @Mapping(target = "id", source = "id")
    Hotel toDomainWithId(HotelDto hotelDto, String id);

}
