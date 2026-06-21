package com.thangvd.cinepass.service;


import com.thangvd.cinepass.repository.SeatRepository;
import com.thangvd.cinepass.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TicketCleanupScheduler {

    @Autowired
    private final TicketRepository ticketRepository;

    @Autowired
    private SeatRepository seatRepository;


    public TicketCleanupScheduler(TicketRepository ticketRepository,
                                  SeatRepository seatRepository) {
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
    }

    //    quét 1 phút/lần
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredTicket() {
//        mốc thời gian 60s tính từ thời điểm hiện tại
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);

//        update các vé quá hạn sang trạng thái canceled
        int updateCount = ticketRepository.cancelExpiredTicketsBulk(threshold);

        if (updateCount > 0) {
            System.out.println("Đã hủy tự động " + updateCount + " vé quá hạn giữ chỗ!");
        }
    }
}
