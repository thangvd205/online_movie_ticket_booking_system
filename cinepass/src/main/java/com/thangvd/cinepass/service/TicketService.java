package com.thangvd.cinepass.service;

import com.thangvd.cinepass.dto.SeatStatusResponse;
import com.thangvd.cinepass.exception.ResourceNotFoundException;
import com.thangvd.cinepass.exception.SeatAlreadyBookedException;
import com.thangvd.cinepass.exception.TicketAccessDeniedException;
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

    //tính giá theo hàng ghế, tính ở server thay vì client gửi lên để tránh gian lận
    private static final Map<String, Double> SEAT_TYPE_PRICE = Map.of(
            "REGULAR", 80000.0,
            "VIP", 150000.0,
            "COUPLE", 310000.0
    );
    private static final Double DEFAULT_SEAT_PRICE = 80000.0;
    private static final long HOLD_MINUTES = 15; // thời gian giữ chỗ chờ thanh toán là 15 phút

    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    public TicketService(TicketRepository ticketRepository, ShowtimeRepository showtimeRepository, SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
    }

    private double resolvePrice(Seat seat) {
        return SEAT_TYPE_PRICE.getOrDefault(seat.getSeatType(), DEFAULT_SEAT_PRICE);
    }


        @Transactional
    public Ticket bookTicketByIds(Long showtimeId, Long seatId, Long userId) {
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
        ticket.setPrice(resolvePrice(seat));
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
    public Ticket confirmePayment(Long ticketId, Long requestingUserId) {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Không tìm thấy vé đã đặt!"));

        //chống IDOR: chính chủ của vé mới được thanh toán
        if(ticket.getUserId() == null ||
                !ticket.getUserId().equals(requestingUserId)) {
            throw new TicketAccessDeniedException("Bạn không có quyền thanh toán vé này!");
        }

        //nếu đã confirmed thì trả về nguyên trạng, không sinh mã mới để ghi đè mã cũ
        if("CONFIRMED".equals(ticket.getStatus())) {
            return ticket;
        }

        if("CANCELLED".equals(ticket.getStatus())) {
            throw new RuntimeException("Vé đã bị hủy, không thể thanh toán!");
        }

        // kiểm tra vé có còn trong trạng thái holding không
        if("HOLDING".equals(ticket.getStatus()) && ticket.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Thời gian đặt vé đã hết, vui lòng thực hiện thanh toán lại!");
        }

        // cập nhật trạng thái và sinh mã vé mới
        ticket.setStatus("CONFIRMED");

        // sinh mã ngẫu nhiên với 6 ký tự
        String randomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        ticket.setBookingCode("CT-" + randomCode);

        // lưu lại vào db
        return ticketRepository.save(ticket);
    }


//    LOGIC: xử lý sơ đồ ghế động
//    lấy toàn bộ ghế so sánh với danh sách từ db
//    gán trạng thái sáng/tối màu cho ghế
    public List<SeatStatusResponse> getSeatFlowByShowtime(Long showtimeId) {

        //lấy danh sách vé còn hiệu lực của suất chiếu
        List<Ticket> validTickets = ticketRepository.findValidTicketsByShowtime(showtimeId, LocalDateTime.now());

        //chuyển danh sách vé thành map với key là seatId để tìm kiếm với mức độ phức tạp O(1)
        Map<Long, String> seatStatusMap = validTickets.stream().collect(Collectors.toMap(ticket -> ticket.getSeat().getId(), Ticket::getStatus));

        //tìm suất chiếu thuộc phòng chiếu nào
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy suất chiếu!"));
        Long roomId = showtime.getRoom().getId();

        //lấy danh sách ghế của phòng chiếu
        List<Seat> allSeatsInRoom = seatRepository.findByRoomId(roomId);

        // duyệt qua từng ghế để phân loại trạng thái sáng/tối
        List<SeatStatusResponse> layout = new ArrayList<>();
        for (Seat seat : allSeatsInRoom) {
            //mặc định = trắng
            String status = "AVAILABLE";

            //kiểm tra: nếu ghế nằm trong map thì lấy trạng thái của vé đó(holding/confirmed), nếu không thì vẫn là AVAILABLE
            if (seatStatusMap.containsKey(seat.getId())) {
                status = seatStatusMap.get(seat.getId());
            }
            layout.add(new SeatStatusResponse(seat.getId(), seat.getSeatNumber(), seat.getSeatType(), status));
        }

        return layout;
    }

}
