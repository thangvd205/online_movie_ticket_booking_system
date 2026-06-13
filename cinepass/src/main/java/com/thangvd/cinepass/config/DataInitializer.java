package com.thangvd.cinepass.config;


import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.repository.CinemaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CinemaRepository repository) {
        return args -> {

//            kiểm tra nếu database chưa có  dữ liệu thì chèn vào tránh bị trùng lặp
            if (repository.count() == 0) {
                repository.save(new Cinema("Beta Cinemas Thanh Xuân", "Tầng hầm B1, tòa nhà Golden West, 2 phường Lê Văn Thiên, Thanh Xuân, Hà Nội"));
                repository.save(new Cinema("CGV Vincom Mega Mall TimeCity", "Vincom Mega Mall, 45 phường Minh Khai, khu đô thị TimeCity, Vĩnh Tuy, Hà Nội"));
                repository.save(new Cinema("Beta Cimenas Mỹ Đình", "Golden palace, Tầng hầm B1, Tòa nhà, Đường Mễ Trì, Từ Liêm, Hà Nội, Việt Nam"));
                repository.save(new Cinema("Beta Giải Phóng", "IP2 toà Imperial, 360 Đường Giải Phóng, Phương Liệt, Hà Nội, Việt Nam"));
                System.out.println("Khởi tạo thành công dữ liệu!");
            }
        };
    }
}
