package com.thangvd.cinepass.controller;


import com.thangvd.cinepass.dto.SeatStatusResponse;
import com.thangvd.cinepass.dto.TicketResponse;
import com.thangvd.cinepass.model.Ticket;
import com.thangvd.cinepass.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*") // cho phép font-end kết nối thoải mái không bị chặn cross

public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

//    1. api xác nhận thanh toán thành công -> đổi trạng thái sang CONFIRMED và xuất vé điện tử
    @PostMapping("/tickets/{id}/confirm")
    public ResponseEntity<TicketResponse> confirmPayment(@PathVariable("id") Long ticketId) {
        Ticket confirmedTicket = ticketService.confirmePayment(ticketId);

//        bọc thực thể vào DTO sạch để trả về bằng chứng đặt vé đầy đủ thông tin
        return ResponseEntity.ok(new TicketResponse(confirmedTicket));
    }

//    2. api lấy sơ đồ ghế sáng(AVAILABLE)/vàng(HOLDING)/tối(CONFIRMED) của suất chiếu
    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatStatusResponse>> getRoomSeatLayout(@PathVariable("showtimeId") Long showtimeId) {
        List<SeatStatusResponse> seatLayout = ticketService.getSeatFlowByShowtime(showtimeId);
        return ResponseEntity.ok(seatLayout);
    }

//    bắt lỗi RuntimeException xảy ra trong controller, hàm sẽ tự động kích hoạt
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
//        trả về thông báo lỗi JSON: {"error" : "Thông báo lỗi thực tế"}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Map.of("error", ex.getMessage())));
    }


}
