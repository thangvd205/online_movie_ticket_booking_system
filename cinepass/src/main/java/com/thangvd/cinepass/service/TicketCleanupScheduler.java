package com.thangvd.cinepass.service;


import com.thangvd.cinepass.repository.SeatRepository;
import com.thangvd.cinepass.repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TicketCleanupScheduler {

   private final TicketRepository ticketRepository;
   private final SeatRepository seatRepository;

   public TicketCleanupScheduler(TicketRepository ticketRepository,
                                 SeatRepository seatRepository) {
       this.ticketRepository = ticketRepository;
       this.seatRepository = seatRepository;
   }

   @Scheduled(fixedRate = 60000)
   @Transactional
   public void cleanupExpiredTicket() {
       LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);

       int updateCount = ticketRepository.cancelExpiredTicketsBulk(threshold);

       if (updateCount > 0) {
           System.out.println("Đã hủy tự động " + updateCount + " vé quá hạn giữ chỗ!");
       }
   }
}
