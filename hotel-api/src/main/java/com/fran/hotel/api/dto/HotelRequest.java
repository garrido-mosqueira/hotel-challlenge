package com.fran.hotel.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HotelRequest {

    private String id;
    private String name;
    private List<String> roomIds;

}
