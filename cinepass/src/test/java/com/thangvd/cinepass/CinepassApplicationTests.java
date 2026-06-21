package com.thangvd.cinepass;

import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.model.Seat;
import com.thangvd.cinepass.model.Showtime;
import com.thangvd.cinepass.repository.SeatRepository;
import com.thangvd.cinepass.repository.ShowtimeRepository;
import com.thangvd.cinepass.repository.TicketRepository;
import com.thangvd.cinepass.service.TicketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class CinepassApplicationTests {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;

    private Showtime savedShowtime;
    private Seat savedSeat;

    @BeforeEach
    void setUpData() {
        // Bản chất: Trước mỗi ca test, dọn sạch dữ liệu cũ để tránh lỗi trùng lặp khóa hoặc sai lệch count()
        ticketRepository.deleteAll();
        showtimeRepository.deleteAll();
        seatRepository.deleteAll();

        // Tự tạo dữ liệu mồi mới tinh, không phụ thuộc vào ID = 1 bên ngoài DB nữa
        Showtime dummyShowtime = new Showtime();
        dummyShowtime.setMovieTitle("Phim Bom Tấn CinePass 2026");
        dummyShowtime.setStartTime(LocalDateTime.now());
        this.savedShowtime = showtimeRepository.save(dummyShowtime); // DB tự sinh ID bao nhiêu ta dùng bấy nhiêu

        Seat dummySeat = new Seat();
        dummySeat.setSeatNumber("A1");
        dummySeat.setSeatType("VIP");
        this.savedSeat = seatRepository.save(dummySeat);
    }

    @Test
    void testConcurrentBooking() throws InterruptedException {
        // Kiểm tra log để chắc chắn data mồi đã lên
        System.out.println("====== ĐÃ KHỞI TẠO SHOWTIME ID THỰC TẾ: " + savedShowtime.getId());

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Latch 1: Ép 2 luồng xuất phát cùng lúc tại vạch đích
        CountDownLatch startLatch = new CountDownLatch(1);

        // Latch 2: Bản chất thay thế cho Thread.sleep. Nó bắt hàm test chính phải đợi đúng lúc
        // cả 2 luồng con thực hiện xong nhiệm vụ (xuống 0) thì mới chạy tiếp xuống đoạn Assert kết quả.
        CountDownLatch endLatch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger faildCount = new AtomicInteger(0);

        Runnable bookingTask = () -> {
            try {
                startLatch.await(); // Đứng đợi lệnh phát súng

                // Truyền đối tượng đã được lưu thực tế trong DB vào
                ticketService.bookTicket(savedShowtime, savedSeat, 100000.0);
                successCount.incrementAndGet();
            } catch (SeatAlreadyBookedException e) {
                faildCount.incrementAndGet(); // Bị chặn do tranh chấp ghi trùng ghế
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown(); // Báo cáo cho endLatch biết luồng này đã chạy xong
            }
        };

        // Nạp 2 luồng vào Thread Pool
        executor.submit(bookingTask);
        executor.submit(bookingTask);

        // Phát súng! Cho 2 người dùng lao vào bấm đặt ghế cùng 1 mili-giây
        startLatch.countDown();

        // Thay vì Thread.sleep(2000), ta dùng await() thông minh, luồng con xong là chạy tiếp ngay lập tức
        endLatch.await();
        executor.shutdown();

        // In kết quả kiểm tra
        System.out.println("Số ghế đặt thành công: " + successCount.get());
        System.out.println("Số lượng bị chặn (đặt ghế bị trùng lặp): " + faildCount.get());

        // Kiểm tra tính toàn vẹn dữ liệu: Hệ thống chịu tải đa luồng chuẩn thì chỉ được phép có duy nhất 1 vé tạo thành công
        long ticketCount = ticketRepository.count();
        org.junit.jupiter.api.Assertions.assertEquals(1, ticketCount, "Thất bại: Số lượng vé lưu trong DB phải đúng bằng 1!");
    }

    @AfterEach
    void tearDown() {
        // Chạy xong xóa sạch dữ liệu mồi, trả lại môi trường DB sạch sẽ cho các ca test khác
        ticketRepository.deleteAll();
    }
}