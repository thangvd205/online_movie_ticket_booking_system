package com.thangvd.cinepass.repository;

import com.thangvd.cinepass.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {

//    lệnh truy vấn
//    Tìm theo tên, tìm theo địa chỉ, tìm kiếm gần đúng(chứa từ khoá)
    @Query("SELECT c FROM Cinema c WHERE " +
            "(:name IS NULL OR c.name LIKE %:name%) AND " +
            "(:address IS NULL OR c.address LIKE %:address%)")
    List<Cinema> searchCinemas (@Param("name") String name, @Param("address") String address);
}
