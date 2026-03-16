package com.fran.hotel.api.dto;

public class RoomDto {
    private String id;
    private String roomNumber;
    private String type;

    public RoomDto() {}

    public RoomDto(String id, String roomNumber, String type) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
