package com.thangvd.cinepass.repository;

import com.thangvd.cinepass.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByShowtimeIdAndSeatId(Long showtimeId, Long seatId);

    @Modifying
    @Query
    void deleteByStatusAndExpiryTimeBefore(String status, LocalDateTime now);

//    thêm truy vấn: lấy danh sách vé hợp lệ(đã confirmed hoặc đang holding chưa hết hạn)
    @Query("SELECT t FROM Ticket t WHERE t.showtime.id = :showtimeId " +
            "AND (t.status = 'CONFIRMED' OR (t.status = 'HOLDING' AND t.expiryTime > :now))")
    List<Ticket> findValidTicketsByShowtime(@Param("showtimeId") Long ShowtimeId, @Param("now") LocalDateTime now);
}
