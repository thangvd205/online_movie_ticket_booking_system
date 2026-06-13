package com.thangvd.cinepass.service;

import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    @Override
    public List<Cinema> getAllCinemas() {
        return cinemaRepository.findAll();
    }
    @Override
    public List<Cinema> searchCinemas(String name, String address) {

//        chuẩn hóa dữ liệu: nếu người dùng truyền chuỗi rỗng, biến nó thành null để sql không sai
        String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String searchAddress = (address != null && !address.trim().isEmpty()) ? address.trim() : null;

        return cinemaRepository.searchCinemas(searchName,searchAddress);

    }
}
