package com.thangvd.cinepass.service;

import com.thangvd.cinepass.dto.SeatStatusResponse;
import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.model.Seat;
import com.thangvd.cinepass.model.Showtime;
import com.thangvd.cinepass.model.Ticket;
import com.thangvd.cinepass.repository.SeatRepository;
import com.thangvd.cinepass.repository.ShowtimeRepository;
import com.thangvd.cinepass.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;


    public TicketService(TicketRepository ticketRepository,
                         SeatRepository seatRepository,
                         ShowtimeRepository showtimeRepository) {

        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.showtimeRepository = showtimeRepository;
    }


    @Transactional
    public Ticket bookTicket(Showtime showtime, Seat seat, Double price) {
        // Reload với pessimistic lock để ngăn race condition
        showtime = showtimeRepository.findByIdWithLock(showtime.getId())
                .orElseThrow(() -> new RuntimeException("Suất chiếu không hợp lệ!"));
        seat = seatRepository.findByIdWithLock(seat.getId())
                .orElseThrow(() -> new RuntimeException("Ghế không hợp lệ!"));
        
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
            ticket.setUserId(1L);
            return ticketRepository.save(ticket);
        } catch (DataIntegrityViolationException e) {
            throw new SeatAlreadyBookedException("Ghế đã có người đặt trước đó, vui lòng chọn ghế khác!");
        }
    }

        @Transactional
    public Ticket bookTicketByIds(Long showtimeId, Long seatId, Double price, Long userId) {
        // Dùng pessimistic lock để ngăn race condition giữa 2 luồng
        Showtime showtime = showtimeRepository.findByIdWithLock(showtimeId)
                .orElseThrow(() -> new RuntimeException("Suất chiếu không hợp lệ!"));
        
        Seat seat = seatRepository.findByIdWithLock(seatId)
                .orElseThrow(() -> new RuntimeException("Ghế không hợp lệ!"));

        boolean isSeatTaken = ticketRepository.existsByShowtimeIdAndSeatId(showtimeId, seatId);
        if(isSeatTaken) {
            throw new SeatAlreadyBookedException("Ghế đã bị đặt, vui lòng chọn ghế khác!");
        }

        Ticket ticket = new Ticket();
        ticket.setShowtime(showtime);
        ticket.setSeat(seat);
        ticket.setPrice(price);
        ticket.setBookingTime(LocalDateTime.now());
        ticket.setStatus("HOLDING");
        ticket.setExpiryTime(LocalDateTime.now().plusMinutes(15)); // 15 phút để hoàn tất thanh toán

        try {
            ticket.setUserId(userId);
            return ticketRepository.save(ticket);
        } catch (DataIntegrityViolationException e) {
            throw new SeatAlreadyBookedException("Ghế đã có người đặt trước đó, vui lòng chọn ghế khác!");
        }
    }

    @Transactional
    public Ticket confirmePayment(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Không tìm thấy vé đã đặt!"));

//        kiểm tra vé còn trong thời gian holding không
        if("HOLDING".equals(ticket.getStatus()) && ticket.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian đặt vé đã hết, vui lòng thực hiện lại!");
        }

//        cập nhật trạng thái và sinh mã vé
        ticket.setStatus("CONFIRMED");

//        sinh mã ngẫu nhiên với 6 ký tự
        String randomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ticket.setBookingCode("CT-" + randomCode);

//        lưu lại vào db
        return ticketRepository.save(ticket);
    }


//    LOGIC: xử lý sơ đồ ghế động
//    lấy toàn bộ ghế so sánh với danh sách từ db
//    gán trạng thái sáng/tối màu cho ghế

    public List<SeatStatusResponse> getSeatFlowByShowtime(Long showtimeId) {
//       lấy danh sách vé còn hiệu lực(HOLDING chưa hết hạn hoặc CONFIRMED) của suất chiếu này
        List<Ticket> validTickets = ticketRepository.findValidTicketsByShowtime(showtimeId, LocalDateTime.now());

//        chuyển danh sách vé thành Map với key là seatId để tìm kiếm với độ phức tạp 0(1)
        Map<Long, String> seatStatusMap = validTickets.stream().collect(Collectors.toMap(t -> t.getSeat().getId(), Ticket::getStatus));

//        tìm suất chiếu thuộc phòng chiếu nào
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new RuntimeException("Suất chiếu không hợp lệ!"));
        Long roomId = showtime.getRoom().getId();

//        lấy toàn bộ danh sách ghế của phòng chiếu
        List<Seat> allSeatsInRoom = seatRepository.findByRoomId(roomId);

//        duyệt qua từng ghế để phân loại trạng thái sáng/tối màu
        List<SeatStatusResponse> layout = new ArrayList<>();
        for(Seat seat : allSeatsInRoom) {
//            mặc định ghế trống ban đầu(màu sáng)
            String status = "AVAILABLE";

//            kiểm tra: nếu ghế nằm trong Map vé thì lấy trạng thái của vé đó(HOLDING/CONFIRMED)
            if(seatStatusMap.containsKey(seat.getId())) {
                status = seatStatusMap.get(seat.getId());
            }
            layout.add(new SeatStatusResponse(seat.getId(), seat.getSeatNumber(), seat.getSeatType(), status));
        }

        return layout;
    }
}
