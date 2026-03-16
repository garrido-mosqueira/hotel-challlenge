package com.fran.hotel.persistence.mapper;

import com.fran.expedia.domain.model.Price;
import com.fran.hotel.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PricePersistenceMapper {

    Price toDomain(PriceEntity entity);

}
