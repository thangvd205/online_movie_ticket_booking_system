package com.thangvd.cinepass.repository;

import com.thangvd.cinepass.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByShowtimeIdAndSeatId(Long showtimeId, Long seatId);

    @Modifying
    @Query
    void deleteByStatusAndExpiryTimeBefore(String status, LocalDateTime now);
}
