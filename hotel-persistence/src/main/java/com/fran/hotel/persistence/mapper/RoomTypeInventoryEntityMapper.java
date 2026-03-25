package com.fran.hotel.persistence.mapper;

import com.fran.hotel.domain.model.RoomTypeInventory;
import com.fran.hotel.persistence.entity.RoomTypeInventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomTypeInventoryEntityMapper {

    @Mapping(target = "hotel.id", source = "hotelId")
    RoomTypeInventoryEntity toEntity(RoomTypeInventory domain);

    @Mapping(target = "hotelId", source = "hotel.id")
    RoomTypeInventory toDomain(RoomTypeInventoryEntity entity);

}
