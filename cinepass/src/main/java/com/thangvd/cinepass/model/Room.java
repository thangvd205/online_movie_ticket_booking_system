package com.thangvd.cinepass.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private Integer totalseats;

//    (N)rooms-(1)rạp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false) // tên cột khóa ngoại
    private Cinema cinema;

//    khởi tạo constructor không tham số
    public Room() {}

//    khởi tạo constructor có tham số
    public Room(String name, Integer totalseats, Cinema cinema) {
        this.name = name;
        this.totalseats = totalseats;
        this.cinema = cinema;
    }

//    khởi tạo getter/setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalseats() {
        return totalseats;
    }

    public void setTotalseats(Integer totalseats) {
        this.totalseats = totalseats;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}
