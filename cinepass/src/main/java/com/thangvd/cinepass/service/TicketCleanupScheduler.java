package com.thangvd.cinepass.service;


import com.thangvd.cinepass.repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TicketCleanupScheduler {
    private final TicketRepository ticketRepository;

    public TicketCleanupScheduler(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

//    cứ 10 giây(10000 ms) hệ thống quét db tự động 1 lần
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cleanupExpiredHoldings() {
//        xóa hẳn vé có holding quá giờ
        ticketRepository.deleteByStatusAndExpiryTimeBefore("HOLDING", LocalDateTime.now());
    }
}
