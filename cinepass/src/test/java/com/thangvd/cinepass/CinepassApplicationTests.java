package com.thangvd.cinepass;

import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.model.Seat;
import com.thangvd.cinepass.model.Showtime;
import com.thangvd.cinepass.repository.SeatRepository;
import com.thangvd.cinepass.repository.ShowtimeRepository;
import com.thangvd.cinepass.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

	@Test
	void testConcurrenBooking() throws InterruptedException {
        Showtime realShowtime = showtimeRepository.findById(1L).orElseThrow();
        Seat realSeat = seatRepository.findById(1L).orElseThrow();


//        Tạo 2 luồng thread đại diện 2 người bấm cùng lúc
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1); // ép 2 luồng xuất phát cùng lúc

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger faildCount = new AtomicInteger(0);

//        định nghĩa hành động đặt ghế
        Runnable bookingTask = () -> {
            try {
                latch.await(); // đợi lệnh
                ticketService.bookTicket(realShowtime, realSeat, 100000.0);
                successCount.incrementAndGet(); //đặt thành công tăng 1

            }catch (SeatAlreadyBookedException e) {
                faildCount.incrementAndGet(); //trùng lặp sẽ bị chặn
            }catch (Exception e) {
                e.printStackTrace();
            }
        };

//        nạp dữ liệu vào hệ thống
        executor.submit(bookingTask);
        executor.submit(bookingTask);

//        phát lệnh cho cả 2 người dùng bấm đặt ghế cùng lúc
        latch.countDown();

//        đợi 2 luồng xử lý
        Thread.sleep(2000);
        executor.shutdown();

//        in kết quả
        System.out.println("Số ghế đặt thành công: " + successCount.get());
        System.out.println("Số lượng bị chặn(đặt ghế bị trùng lặp): " + faildCount.get());
	}

}
