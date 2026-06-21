package com.thangvd.cinepass.dto;

import com.thangvd.cinepass.model.Ticket;

import java.time.LocalDateTime;

public class TicketResponse {
    private Long id;
    private String bookingCode;
    private String movieTitle;
    private String cinemaName;
    private String cinemaAddress;
    private String roomName;
    private String seatNumber;
    private Double price;
    private LocalDateTime bookingTime;
    private LocalDateTime startTime;
    private String status;
    private LocalDateTime expiryTime;

//  chuyển đổi từ Entity sang DTO response sạch sẽ
public TicketResponse(Ticket ticket) {
    this.id = ticket.getId();
    this.bookingCode = ticket.getBookingCode();
    this.movieTitle = ticket.getShowtime().getMovieTitle();
    this.cinemaName = ticket.getShowtime().getRoom().getCinema().getName();
    this.cinemaAddress = ticket.getShowtime().getRoom().getCinema().getAddress();
    this.roomName = ticket.getShowtime().getRoom().getName();
    this.seatNumber = ticket.getSeat().getSeatNumber();
    this.price = ticket.getPrice();
    this.bookingTime = ticket.getBookingTime();
    this.startTime = ticket.getShowtime().getStartTime();
    this.status = ticket.getStatus();
    this.expiryTime = ticket.getExpiryTime();
    }

//    getter & setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
}
