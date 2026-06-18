package com.thangvd.cinepass.dto;

public class SeatStatusResponse {
    private Long seatId;
    private String seatNumber;
    private String seatType;
    private String status; // AVAILABLE, HOLDING, CONFIRMED

    public SeatStatusResponse(Long seatId, String seatNumber, String seatType, String status) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = status;
    }

//    getter cho thư viện jackson chuyển đổi sang JSON


    public Long getSeatId() {
        return seatId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public String getStatus() {
        return status;
    }
}
