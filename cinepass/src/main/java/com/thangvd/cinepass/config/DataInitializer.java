package com.thangvd.cinepass.config;


import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.model.Room;
import com.thangvd.cinepass.model.Seat;
import com.thangvd.cinepass.model.Showtime;
import com.thangvd.cinepass.repository.CinemaRepository;
import com.thangvd.cinepass.repository.RoomRepository;
import com.thangvd.cinepass.repository.SeatRepository;
import com.thangvd.cinepass.repository.ShowtimeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CinemaRepository cinemaRepository,
                                   RoomRepository roomRepository,
                                   ShowtimeRepository showtimeRepository,
                                   SeatRepository seatRepository) {
        return args -> {

//            kiểm tra nếu database chưa có  dữ liệu thì chèn vào tránh bị trùng lặp
            if (cinemaRepository.count() == 0) {
                // Tạo các Cinema
                Cinema cinema1 = cinemaRepository.save(new Cinema("Beta Cinemas Thanh Xuân", "Tầng hầm B1, tòa nhà Golden West, 2 phường Lê Văn Thiên, Thanh Xuân, Hà Nội"));
                Cinema cinema2 = cinemaRepository.save(new Cinema("CGV Vincom Mega Mall TimeCity", "Vincom Mega Mall, 45 phường Minh Khai, khu đô thị TimeCity, Vĩnh Tuy, Hà Nội"));
                Cinema cinema3 = cinemaRepository.save(new Cinema("Beta Cinemas Mỹ Đình", "Golden palace, Tầng hầm B1, Tòa nhà, Đường Mễ Trì, Từ Liêm, Hà Nội, Việt Nam"));
                Cinema cinema4 = cinemaRepository.save(new Cinema("Beta Giải Phóng", "IP2 toà Imperial, 360 Đường Giải Phóng, Phương Liệt, Hà Nội, Việt Nam"));

                // Tạo Room cho cinema1
                Room room1 = new Room("Phòng Chiếu 01 (IMAX)", 100, cinema1);
                Room savedRoom1 = roomRepository.save(room1);

                // Tạo Room cho cinema2
                Room room2 = new Room("Phòng Chiếu 02 (4DX)", 80, cinema2);
                Room savedRoom2 = roomRepository.save(room2);

                // Tạo Showtime cho room1
                Showtime showtime1 = new Showtime();
                showtime1.setMovieTitle("Phim Bom Tấn CinePass 2026");
                showtime1.setStartTime(LocalDateTime.now().plusDays(1).withHour(19).withMinute(0).withSecond(0));
                showtime1.setRoom(savedRoom1);
                Showtime savedShowtime1 = showtimeRepository.save(showtime1);

                // Tạo Showtime cho room2
                Showtime showtime2 = new Showtime();
                showtime2.setMovieTitle("Phim Kinh Dị 2026");
                showtime2.setStartTime(LocalDateTime.now().plusDays(2).withHour(20).withMinute(0).withSecond(0));
                showtime2.setRoom(savedRoom2);
                Showtime savedShowtime2 = showtimeRepository.save(showtime2);

                // Tạo Seat cho room1
                for (int i = 1; i <= 10; i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber("A" + i);
                    seat.setSeatType(i <= 5 ? "VIP" : "STANDARD");
                    seat.setRoom(savedRoom1);
                    seatRepository.save(seat);
                }

                // Tạo Seat cho room2
                for (int i = 1; i <= 10; i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber("B" + i);
                    seat.setSeatType(i <= 4 ? "VIP" : "STANDARD");
                    seat.setRoom(savedRoom2);
                    seatRepository.save(seat);
                }

                System.out.println("✅ Khởi tạo thành công dữ liệu!");
                System.out.println("   - 4 Cinema");
                System.out.println("   - 2 Room");
                System.out.println("   - 2 Showtime");
                System.out.println("   - 20 Seat");
            }
        };
    }
}
