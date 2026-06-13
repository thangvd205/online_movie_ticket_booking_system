package com.thangvd.cinepass.repository;

import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Cinema, Long> {
    List<Room> findByCinemaID(Long cinemaID);
}
