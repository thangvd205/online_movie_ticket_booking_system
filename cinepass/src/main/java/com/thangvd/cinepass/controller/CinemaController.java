package com.thangvd.cinepass.controller;

import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.service.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cinema")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

//    lấy toàn bộ danh sách rạp phim
    @GetMapping
    public List<Cinema> getAllCinemas() {
        return cinemaService.getAllCinemas();
    }

//    API tìm kiếm
    @GetMapping("/search")
    public List<Cinema> searchCinemas(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address) {

        return cinemaService.searchCinemas(name, address);
    }
}
