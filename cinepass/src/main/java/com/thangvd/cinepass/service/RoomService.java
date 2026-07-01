package com.thangvd.cinepass.service;


import com.thangvd.cinepass.dto.RoomRequest;
import com.thangvd.cinepass.dto.RoomResponse;
import com.thangvd.cinepass.model.Cinema;
import com.thangvd.cinepass.model.Room;
import com.thangvd.cinepass.repository.CinemaRepository;
import com.thangvd.cinepass.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;

    public RoomService(RoomRepository roomRepository, CinemaRepository cinemaRepository) {
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
    }

//    1 lấy tất cả các phòng trong 1 rạp
    @Transactional(readOnly = true)
    public List<RoomResponse> getRoomsByCinema(Long cinemaId) {
        if (!cinemaRepository.existsById(cinemaId)) {
            throw new RuntimeException("Không tìm thấy rạp: " + cinemaId);
        }

        return roomRepository.findByCinemaId(cinemaId).stream()
                .map(room -> new RoomResponse(room.getId(), room.getName(), room.getTotalSeats(),
                        room.getCinema().getName())).collect(Collectors.toList());
    }

//    2 thêm phòng chiếu
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Cinema cinema = cinemaRepository.findById(request.getCinemaID()).orElseThrow(() -> new RuntimeException("Không thể thêm phòng. Không tìm thấy rạp: " +request.getCinemaID()));

        Room room = new Room(request.getName(), request.getTotalSeats(), cinema);
        Room savedRoom = roomRepository.save(room);

        return new RoomResponse(savedRoom.getId(), savedRoom.getName(), savedRoom.getTotalSeats(), cinema.getName());
    }

//    3 xóa phòng chiếu
    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phòng: " + id));

        roomRepository.delete(room);
    }
}
