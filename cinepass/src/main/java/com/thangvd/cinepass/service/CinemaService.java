package com.thangvd.cinepass.service;


import com.thangvd.cinepass.dto.CinemaRequest;
import com.thangvd.cinepass.dto.CinemaResponse;
import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.repository.CinemaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CinemaService {
    private final CinemaRepository cinemaRepository;

    public CinemaService(CinemaRepository cinemaRepository) {
        this.cinemaRepository = cinemaRepository;
    }

//    1.lấy danh sách các rạp
    @Transactional(readOnly = true)
    public List<CinemaResponse> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(cinema -> new CinemaResponse(cinema.getId(), cinema.getName(), cinema.getAddress()))
                .collect(Collectors.toList());
    }

//    2. lấy chi tiết rạp theo id
    @Transactional(readOnly = true)
    public CinemaResponse getCinemaByID(Long id) {
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy rạp: " + id));
        return new CinemaResponse(cinema.getId(), cinema.getName(), cinema.getAddress());
    }

//    3. thêm mới rạp
    @Transactional
    public CinemaResponse createCinema(CinemaRequest request) {
        Cinema cinema = new Cinema(request.getName(), request.getAddress());
        Cinema savedCinema = cinemaRepository.save(cinema);
        return new CinemaResponse(savedCinema.getId(), savedCinema.getName(), savedCinema.getAddress());
    }

//    4. cập nhật thông tin rạp phim
    @Transactional
    public CinemaResponse updatedCinema(Long id, CinemaRequest request) {
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy rạp: " + id));

        cinema.setName(request.getName());
        cinema.setAddress(request.getAddress());
        Cinema updatedCinema = cinemaRepository.save(cinema);

        return new CinemaResponse(updatedCinema.getId(), updatedCinema.getName(), updatedCinema.getAddress());
    }

//    5. logic xóa rạp
    public void deleteCinema(Long id) {
        throw new RuntimeException("Không được phép xóa rạp ra khỏi hệ thống!");
    }
}
