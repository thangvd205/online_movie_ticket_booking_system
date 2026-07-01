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

    // lấy danh sách vé hợp lệ
    @Query("SELECT t FROM Ticket t WHERE t.showtime.id = :showtimeId " +
            "AND (t.status = 'CONFIRMED' OR (t.status = 'HOLDING' AND t.expiryTime > :now))")
    List<Ticket> findValidTicketsByShowtime(@Param("showtimeId") Long showtimeId, @Param("now") LocalDateTime now);

    //lịch sử vé người dùng
    List<Ticket> findByUserIdOrderByIdDesc(Long userId);

    //tối ưu performance: ép trạng thái về hết hạn hàng loạt, dựa trên expiryTime từng vé
    @Modifying
    @Query("UPDATE Ticket t SET t.status = 'CANCELLED' WHERE t.status = 'HOLDING' AND t.expiryTime <= :now")
    int cancelExpiredTicketsBulk(@Param("now") LocalDateTime now);
}
