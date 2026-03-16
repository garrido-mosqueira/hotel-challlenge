package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.HotelDto;
import com.fran.hotel.domain.model.Hotel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    HotelDto toDto(Hotel hotel);

    Hotel toDomain(HotelDto hotelDto);

}
