package com.thangvd.cinepass.service;

import com.thangvd.cinepass.model.Cinema;

import java.util.List;

public interface CinemaService {
    List<Cinema> getAllCinemas();
    List<Cinema> searchCinemas(String name, String address);
}
