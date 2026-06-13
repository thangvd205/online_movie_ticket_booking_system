package com.thangvd.cinepass.dto;

public class RoomRequest {
    private String name;
    private Integer totalSeats;
    private Long cinemaID;  // đặt id để biết phòng này trỏ vào rạp nào


    public RoomRequest() {}

    public RoomRequest(String name, Integer totalSeats, Long cinemaID) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.cinemaID = cinemaID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Long getCinemaID() {
        return cinemaID;
    }

    public void setCinemaID(Long cinemaID) {
        this.cinemaID = cinemaID;
    }
}
