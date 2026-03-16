package com.fran.hotel.api.mapper;

import com.fran.hotel.api.dto.PriceResponse;
import com.fran.hotel.domain.model.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceDTOMapper {

    @Mapping(source = "pvp", target = "price")
    PriceResponse toResponse(Price task);

}