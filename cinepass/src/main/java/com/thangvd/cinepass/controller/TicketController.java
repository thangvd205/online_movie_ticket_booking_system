package com.thangvd.cinepass.controller;


import com.thangvd.cinepass.model.Ticket;
import com.thangvd.cinepass.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

//    api đặt vé(nhận các thông tin cơ bản)
    @PostMapping("/book")
    public ResponseEntity<Ticket> bookTicet(@RequestParam Long showtimeId, @RequestParam Long seatId, @RequestParam Double price) {

//       tạo object giả lập truyền qua service để check
        com.thangvd.cinepass.model.Showtime mockShowtime = new com.thangvd.cinepass.model.Showtime();
        mockShowtime.setId(showtimeId);

        com.thangvd.cinepass.model.Seat mockSeat = new com.thangvd.cinepass.model.Seat();
        mockSeat.setId(seatId);

        Ticket bookTicket = ticketService.bookTicket(mockShowtime, mockSeat, price);
        return ResponseEntity.ok(bookTicket);
    }

}
