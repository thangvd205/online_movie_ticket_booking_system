package com.thangvd.cinepass.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cines")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //tự động tăng ID
    private Long id;

//    ép kiểu để lưu tên rạp tiếng việt không bị lỗi
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 300)
    private String address;

//    (1)cinemas-(N)room
//    mappedBy trỏ tới biến "cinema" nằm trong class Room
//    CascadeType.ALL: khi xóa rạp sẽ xóa hết các phòng thuộc vào rạp đó
    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms;

//    khởi tạo constructor không tham số
    public Cinema() {}

//    khởi tạo constructor có tham số
    public Cinema(String name, String address) {
        this.name = name;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}
