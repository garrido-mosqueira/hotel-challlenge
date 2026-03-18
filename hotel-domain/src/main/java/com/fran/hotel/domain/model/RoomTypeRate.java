package com.fran.hotel.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RoomTypeRate(
    Long hotelId,
    LocalDate date,
    BigDecimal rate
) {}
