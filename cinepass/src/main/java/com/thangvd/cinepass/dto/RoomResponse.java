package com.thangvd.cinepass.dto;

public class RoomResponse {
    private Long id;
    private String name;
    private Integer totalSeats;
    private String cinemaName; // trả về tên cho fontend

    public RoomResponse(Long id, String name, Integer totalSeats, String cinemaName) {
        this.id = id;
        this.name = name;
        this.totalSeats = totalSeats;
        this.cinemaName = cinemaName;
    }

//    khởi tạo getter/setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }
}
