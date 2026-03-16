package com.fran.hotel.persistence.repository;

import com.fran.hotel.persistence.entity.PriceEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface PriceJpaRepository extends ReactiveCrudRepository<PriceEntity, Long> {

    @Query("SELECT * FROM PRICES p WHERE p.BRAND_ID = :brandId AND p.PRODUCT_ID = :productId " +
            "AND :applicationDate BETWEEN p.START_DATE AND p.END_DATE " +
            "ORDER BY p.PRIORITY DESC LIMIT 1")
    Mono<PriceEntity> findTopByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
            Long brandId,
            Long productId,
            LocalDateTime applicationDate);

}
