package com.thangvd.cinepass.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "cinema_id", nullable = false)
   private Cinema cinema;

   public Room() {}

   public Room(String name, Integer totalSeats, Cinema cinema) {
       this.name = name;
       this.totalSeats = totalSeats;
       this.cinema = cinema;
   }
}
