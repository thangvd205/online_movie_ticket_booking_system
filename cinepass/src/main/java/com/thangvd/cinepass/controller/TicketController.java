package com.thangvd.cinepass.controller;


import com.thangvd.cinepass.dto.SeatStatusResponse;
import com.thangvd.cinepass.dto.ShowtimeResponse;
import com.thangvd.cinepass.dto.TicketRequest;
import com.thangvd.cinepass.dto.TicketResponse;
import com.thangvd.cinepass.model.Ticket;
import com.thangvd.cinepass.repository.TicketRepository;
import com.thangvd.cinepass.repository.ShowtimeRepository;
import com.thangvd.cinepass.security.JwtUserPrincipal;
import com.thangvd.cinepass.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")

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

    // Lay userId cua nguoi dang dang nhap tu SecurityContext (do JwtFilter set vao)
    private Long currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal jp) {
            return jp.id();
        }
        return null;
    }

    //    1. api lấy danh sách suất chiếu (để xác định showtimeId chính xác)
    @GetMapping("/showtimes")
    public ResponseEntity<List<ShowtimeResponse>> getAllShowtimes() {
        List<ShowtimeResponse> showtimes = showtimeRepository.findAll().stream()
                .map(ShowtimeResponse::new)
                .toList();
        return ResponseEntity.ok(showtimes);
    }

    //    2. api đặt vé (booking ticket)
    @PostMapping(value = "/tickets/book", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> bookTicket(@Valid @RequestBody TicketRequest request) {
        Long userId = currentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        // Gia ve khong con lay tu request cua client, TicketService se tu tinh theo hang ghe
        Ticket ticket = ticketService.bookTicketByIds(request.getShowtimeId(), request.getSeatId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TicketResponse(ticket));
    }

    //    3. api xác nhận thanh toán thành công -> đổi trạng thái sang CONFIRMED và xuất vé điện tử
    @PostMapping(value = "/tickets/{id}/confirm", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> confirmPayment(@PathVariable("id") Long ticketId) {
        Long userId = currentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        Ticket confirmedTicket = ticketService.confirmePayment(ticketId, userId);
        return ResponseEntity.ok(new TicketResponse(confirmedTicket));
    }

    //    4. api lấy sơ đồ ghế sáng(AVAILABLE)/vàng(HOLDING)/tối(CONFIRMED) của suất chiếu
    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatStatusResponse>> getRoomSeatLayout(@PathVariable("showtimeId") Long showtimeId) {
        List<SeatStatusResponse> seatLayout = ticketService.getSeatFlowByShowtime(showtimeId);
        return ResponseEntity.ok(seatLayout);
    }


    //    5. api lấy toàn bộ lịch sử vé đã đặt của người dùng
    @GetMapping("/tickets/history")
    public ResponseEntity<List<TicketResponse>> getTicketHistory() {
        Long userId = currentUserIdOrNull();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Ticket> tickets = ticketRepository.findByUserIdOrderByIdDesc(userId);
        List<TicketResponse> result = tickets.stream().map(TicketResponse::new).toList();
        return ResponseEntity.ok(result);
    }
}