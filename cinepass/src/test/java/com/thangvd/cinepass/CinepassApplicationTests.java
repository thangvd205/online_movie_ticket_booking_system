package com.thangvd.cinepass;

import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.exception.TicketAccessDeniedException;
import com.thangvd.cinepass.model.*;
import com.thangvd.cinepass.repository.*;
import com.thangvd.cinepass.service.TicketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CinepassApplicationTests {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;

    private Showtime savedShowtime;
    private Seat savedSeat;

    @Transactional
    @BeforeEach
    void setUpData() {
        ticketRepository.deleteAll();
        showtimeRepository.deleteAll();
        seatRepository.deleteAll();
        roomRepository.deleteAll();
        cinemaRepository.deleteAll();

        Cinema cinema = new Cinema("CinePass Cinema 2026", "123 Đường Phố, TP HCM");
        Cinema savedCinema = cinemaRepository.save(cinema);

        Room room = new Room("Phòng A", 100, savedCinema);
        Room savedRoom = roomRepository.save(room);

        Showtime dummyShowtime = new Showtime();
        dummyShowtime.setMovieTitle("Phim Bom Tấn CinePass 2026");
        dummyShowtime.setStartTime(LocalDateTime.now());
        dummyShowtime.setRoom(savedRoom);
        this.savedShowtime = showtimeRepository.save(dummyShowtime);

        Seat dummySeat = new Seat();
        dummySeat.setSeatNumber("A1");
        dummySeat.setSeatType("VIP");
        dummySeat.setRoom(savedRoom);
        this.savedSeat = seatRepository.save(dummySeat);
    }

    @Test
    void testConcurrentBooking() throws InterruptedException {
        // Kiểm tra log để chắc chắn data mồi đã lên
        System.out.println("====== ĐÃ KHỞI TẠO SHOWTIME ID THỰC TẾ: " + savedShowtime.getId());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
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

                    //dùng api mà controller thật sự gọi(bookTicketById), userId giả lập = 1L
                    ticketService.bookTicketByIds(savedShowtime.getId(), savedSeat.getId(), 1L);
                    successCount.incrementAndGet(); // Đặt ghế thành công

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

            // In kết quả kiểm tra
            System.out.println("Số ghế đặt thành công: " + successCount.get());
            System.out.println("Số lượng bị chặn (đặt ghế bị trùng lặp): " + faildCount.get());

            // Kiểm tra tính toàn vẹn dữ liệu: Hệ thống chịu tải đa luồng chuẩn thì chỉ được phép có duy nhất 1 vé tạo thành công
            long ticketCount = ticketRepository.count();
            assertEquals(1, ticketCount, "Chỉ có duy nhất 1 vé được tạo thành công trong điều kiện tranh chấp đa luồng!");
        } finally {
            executor.shutdown();
        }
    }

    @Test
    void testTicketPriceIsComputedByServerNotByClient() {
        // mặc định server ghế vip = 150k không liên quan giá client gửi lên
        Ticket ticket = ticketService.bookTicketByIds(savedShowtime.getId(), savedSeat.getId(), 1L);
        assertEquals(150000.0, ticket.getPrice(), "Giá vé phải được tính toán bởi server, không phụ thuộc giá client gửi lên!");
    }

    @Test
     void testConfirmPaymentRejectsWhenNotOwner() {

        //user1 đặt vé
        Ticket ticket = ticketService.bookTicketByIds(savedShowtime.getId(), savedSeat.getId(), 1L);

        //user2 không được phép xác nhận thanh toán vé của user1
        assertThrows(TicketAccessDeniedException.class, () -> ticketService.confirmePayment(ticket.getId(),2L));
    }

    @Test
    void testConfirmPaymentSucceedsForOwnerAndIsIdempotent() {
        Ticket ticket = ticketService.bookTicketByIds(savedShowtime.getId(), savedSeat.getId(), 1L);
        Ticket confirmed = ticketService.confirmePayment(ticket.getId(), 1L);
        assertEquals("CONFIRMED", confirmed.getStatus());
        String firstBookingCode = confirmed.getBookingCode();
        assertNotNull(firstBookingCode);

        // gọi confirm lần 2 không sinh mã vé mới
        Ticket confirmedAgain = ticketService.confirmePayment(ticket.getId(), 1L);
        assertEquals(firstBookingCode, confirmedAgain.getBookingCode());
    }

    @Transactional
    @AfterEach
    void tearDown() {
        //xóa dữ liệu mồi sau khi test xong để tránh ảnh hưởng các test khác
        ticketRepository.deleteAll();
        showtimeRepository.deleteAll();
        seatRepository.deleteAll();
        roomRepository.deleteAll();
        cinemaRepository.deleteAll();
    }
}