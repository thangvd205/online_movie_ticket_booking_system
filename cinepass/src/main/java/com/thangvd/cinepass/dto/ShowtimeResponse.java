package com.thangvd.cinepass.dto;

import com.thangvd.cinepass.model.Showtime;

import java.time.LocalDateTime;

public class ShowtimeResponse {
    private Long id;
    private String movieTitle;
    private LocalDateTime startTime;
    private Long roomId;
    private String roomName;
    private Long cinemaId;
    private String cinemaName;

    public ShowtimeResponse(Showtime showtime) {
        this.id = showtime.getId();
        this.movieTitle = showtime.getMovieTitle();
        this.startTime = showtime.getStartTime();
        this.roomId = showtime.getRoom().getId();
        this.roomName = showtime.getRoom().getName();
        this.cinemaId = showtime.getRoom().getCinema().getId();
        this.cinemaName = showtime.getRoom().getCinema().getName();
    }

    public Long getId() {
        return id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public Long getCinemaId() {
        return cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }
}