-- 1. Xóa sạch dữ liệu cũ
DELETE FROM tickets;
DELETE FROM showtimes;
DELETE FROM seat;
DELETE FROM rooms;
DELETE FROM cines;

-- 2. RESET bộ đếm tự tăng (Identity) của các bảng về 0
DBCC CHECKIDENT ('cines', RESEED, 0);
DBCC CHECKIDENT ('rooms', RESEED, 0);
DBCC CHECKIDENT ('seat', RESEED, 0);
DBCC CHECKIDENT ('showtimes', RESEED, 0);
DBCC CHECKIDENT ('tickets', RESEED, 0);

-- 3. Phần chèn dữ liệu
INSERT INTO cines (name, address)
VALUES (N'CinePass Nguyễn Trãi', N'Thanh Xuân, Hà Nội');

INSERT INTO rooms (name, cinema_id, total_seats)
VALUES (N'Phòng Chiếu 01 (IMAX)', 1, 100);

INSERT INTO seat (seat_number, seat_type, room_id)
VALUES ('A1', 'VIP', 1);

INSERT INTO showtimes (start_time, room_id, movie_title)
VALUES ('2026-06-20T19:00:00', 1, N'Phim Bom Tấn CinePass 2026');