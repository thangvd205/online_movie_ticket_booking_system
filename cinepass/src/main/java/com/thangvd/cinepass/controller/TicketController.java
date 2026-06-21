package com.thangvd.cinepass.controller;


import com.thangvd.cinepass.dto.SeatStatusResponse;
import com.thangvd.cinepass.dto.TicketRequest;
import com.thangvd.cinepass.dto.TicketResponse;
import com.thangvd.cinepass.model.Showtime;
import com.thangvd.cinepass.model.Ticket;
import com.thangvd.cinepass.repository.ShowtimeRepository;
import com.thangvd.cinepass.repository.TicketRepository;
import com.thangvd.cinepass.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.thangvd.cinepass.security.JwtUserPrincipal;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // cho phép font-end kết nối thoải mái không bị chặn cross

public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;

    public TicketController(TicketService ticketService,
                            TicketRepository ticketRepository,
                            ShowtimeRepository showtimeRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
    }

//    0. api lấy danh sách suất chiếu (để xác định showtimeId chính xác)
    @GetMapping("/showtimes")
    public ResponseEntity<List<Showtime>> getAllShowtimes() {
        List<Showtime> showtimes = showtimeRepository.findAll();
        return ResponseEntity.ok(showtimes);
    }

//    0. api đặt vé (booking ticket)
    @PostMapping(value = "/tickets/book", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> bookTicket(@RequestBody TicketRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal jp) {
                userId = jp.id();
            }
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }

            Ticket ticket = ticketService.bookTicketByIds(
                    request.getShowtimeId(),
                    request.getSeatId(),
                    request.getPrice(),
                    userId
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(new TicketResponse(ticket));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

//    1. api xác nhận thanh toán thành công -> đổi trạng thái sang CONFIRMED và xuất vé điện tử
    @PostMapping(value = "/tickets/{id}/confirm", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> confirmPayment(@PathVariable("id") Long ticketId) {
        try {
            Ticket confirmedTicket = ticketService.confirmePayment(ticketId);
            return ResponseEntity.ok(new TicketResponse(confirmedTicket));

        }catch (RuntimeException ex) {
            Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
            if (ticket != null) {
                return ResponseEntity.ok(new TicketResponse(ticket));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

//    2. api lấy sơ đồ ghế sáng(AVAILABLE)/vàng(HOLDING)/tối(CONFIRMED) của suất chiếu
    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatStatusResponse>> getRoomSeatLayout(@PathVariable("showtimeId") Long showtimeId) {
        List<SeatStatusResponse> seatLayout = ticketService.getSeatFlowByShowtime(showtimeId);
        return ResponseEntity.ok(seatLayout);
    }


//    3. api lấy toàn bộ lịch sử vé đã đặt của người dùng
    @GetMapping("/tickets/history")
    public ResponseEntity<List<TicketResponse>> getTicketHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal jp) {
            userId = jp.id();
        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Ticket> tickets = ticketRepository.findByUserIdOrderByIdDesc(userId);
        List<TicketResponse> result = tickets.stream().map(TicketResponse::new).toList();
        return ResponseEntity.ok(result);
    }


//    bắt lỗi RuntimeException xảy ra trong controller, hàm sẽ tự động kích hoạt
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
//        trả về thông báo lỗi JSON: {"error" : "Thông báo lỗi thực tế"}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Map.of("error", ex.getMessage())));
    }


}
