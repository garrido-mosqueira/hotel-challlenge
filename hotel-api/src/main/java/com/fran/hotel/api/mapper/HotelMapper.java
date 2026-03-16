package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.HotelRequest;
import com.fran.hotel.api.dto.HotelResponse;
import com.fran.hotel.domain.model.Hotel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    HotelResponse toResponse(Hotel hotel);

    Hotel toDomain(HotelResponse hotel);

    Hotel toDomain(HotelRequest hotel);

}
