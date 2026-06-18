package com.thangvd.cinepass.service;

import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.model.Seat;
import com.thangvd.cinepass.model.Showtime;
import com.thangvd.cinepass.model.Ticket;
import com.thangvd.cinepass.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service

public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket bookTicket(Showtime showtime, Seat seat, Double price) {
        boolean isSeatTaken = ticketRepository.existsByShowtimeIdAndSeatId(showtime.getId(), seat.getId());
        if(isSeatTaken) {
            throw new SeatAlreadyBookedException("Ghế đã bị đặt, vui lòng chọn ghế khác!");
        }
//        tạo đối tượng ticket trạng thái giữ chỗ
        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeat(seat);
        ticket.setPrice(price);
        ticket.setBookingTime(LocalDateTime.now());
        ticket.setStatus("HOLDING");
        ticket.setExpiryTime(LocalDateTime.now().plusMinutes(1)); // trạng thái chờ người dùng đặt vé trong 1 phút

        try {
//            lưu xuống db
            return ticketRepository.save(ticket);
        } catch (DataIntegrityViolationException e) {
            throw new SeatAlreadyBookedException("Ghế đã có người đặt trước đó, vui lòng chọn ghế khác!");
        }
    }
}
