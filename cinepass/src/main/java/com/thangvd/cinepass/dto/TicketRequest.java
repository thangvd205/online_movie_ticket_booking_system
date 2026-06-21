package com.thangvd.cinepass.dto;

public class TicketRequest {
    private Long showtimeId;
    private Long seatId;
    private Double price;
    private Long userId;

    public TicketRequest() {}

    public TicketRequest(Long showtimeId, Long seatId, Double price, Long userId) {
        this.showtimeId = showtimeId;
        this.seatId = seatId;
        this.price = price;
        this.userId = userId;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

