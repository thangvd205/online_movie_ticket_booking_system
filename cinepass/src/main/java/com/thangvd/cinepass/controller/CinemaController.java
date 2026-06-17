package com.thangvd.cinepass.controller;

import com.thangvd.cinepass.dto.ApiResponse;
import com.thangvd.cinepass.dto.CinemaRequest;
import com.thangvd.cinepass.dto.CinemaResponse;
import com.thangvd.cinepass.service.CinemaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cinema")
public class CinemaController {
    private final CinemaService cinemaService;

    public CinemaController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

//    1 lấy toàn bộ danh sách
    @GetMapping
    public ApiResponse<List<CinemaResponse>> getAllCinemas() {
        List<CinemaResponse> cinemas = cinemaService.getAllCinemas();
        return ApiResponse.success(cinemas, "lấy danh sách rạp phim thành công!");
    }

//    2 lấy chi tiết 1 rạp
    @GetMapping("/{id}")
    public ApiResponse<CinemaResponse> getCinemaByID(@PathVariable Long id) {
        CinemaResponse cinema = cinemaService.getCinemaByID(id);
        return ApiResponse.success(cinema, "Lấy thông tin rạp thành công!");
    }

//    3 thêm mới rạp phim
    @PostMapping
    public ApiResponse<CinemaResponse> createCinema(@RequestBody CinemaRequest request) {
        CinemaResponse newCinema = cinemaService.createCinema(request);
        return ApiResponse.success(newCinema, "Thêm rạp phim thành công!");
    }

//    4 cập nhật rạp phim
    @PutMapping("/{id}")
    public ApiResponse<CinemaResponse> updateCinema(@PathVariable Long id, @RequestBody CinemaRequest request) {
        CinemaResponse updateCinema = cinemaService.updatedCinema(id, request);
        return ApiResponse.success(updateCinema, "Cập nhật thông tin rạp thành công!");
    }

//    5 xóa rạp phim
    @DeleteMapping("/{id}")
    public ApiResponse<CinemaResponse> deleteCinema(@PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return ApiResponse.success(null, "Xóa rạp phim thành công!");
    }
}
