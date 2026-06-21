 -- Xóa dữ liệu cũ (theo thứ tự khóa ngoại)
DELETE FROM tickets;
DELETE FROM showtimes;
DELETE FROM seat;
DELETE FROM rooms;
DELETE FROM cines;

-- Reset identity
DBCC CHECKIDENT ('tickets', RESEED, 0);
DBCC CHECKIDENT ('showtimes', RESEED, 0);
DBCC CHECKIDENT ('seat', RESEED, 0);
DBCC CHECKIDENT ('rooms', RESEED, 0);
DBCC CHECKIDENT ('cines', RESEED, 0);

-- Chèn dữ liệu Cinema
INSERT INTO cines (name, address) VALUES ('CinePass Nguyễn Trãi', 'Thanh Xuân, Hà Nội');
INSERT INTO cines (name, address) VALUES ('CinePass Times City', 'Minh Khai, Vĩnh Tuy, Hà Nội');
INSERT INTO cines (name, address) VALUES ('CinePass Mỹ Đình', 'Từ Liêm, Hà Nội');

-- Chèn dữ liệu Room (cinema_id = 1)
INSERT INTO rooms (cinema_id, name, total_seats) VALUES (1, 'Phòng Chiếu 01 (IMAX)', 100);
INSERT INTO rooms (cinema_id, name, total_seats) VALUES (1, 'Phòng Chiếu 02 (Standard)', 80);

-- Chèn dữ liệu Room (cinema_id = 2)
INSERT INTO rooms (cinema_id, name, total_seats) VALUES (2, 'Phòng Chiếu 03 (4DX)', 90);

-- Chèn dữ liệu Showtime (room_id = 1)
INSERT INTO showtimes (movie_title, room_id, start_time) VALUES ('Phim Bom Tấn CinePass 2026', 1, '2026-06-22 19:00:00');
INSERT INTO showtimes (movie_title, room_id, start_time) VALUES ('Phim Kinh Dị 2026', 1, '2026-06-22 21:00:00');

-- Chèn dữ liệu Showtime (room_id = 2)
INSERT INTO showtimes (movie_title, room_id, start_time) VALUES ('Phim Hài 2026', 2, '2026-06-23 18:30:00');

-- Chèn dữ liệu Showtime (room_id = 3)
INSERT INTO showtimes (movie_title, room_id, start_time) VALUES ('Phim Hành Động 2026', 3, '2026-06-23 19:30:00');

-- Chèn dữ liệu Seat cho room 1 (ghế A1 -> A10)
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A1', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A2', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A3', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A4', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A5', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A6', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A7', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A8', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A9', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (1, 'A10', 'STANDARD');

-- Chèn dữ liệu Seat cho room 2 (ghế B1 -> B10)
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B1', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B2', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B3', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B4', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B5', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B6', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B7', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B8', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B9', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (2, 'B10', 'STANDARD');

-- Chèn dữ liệu Seat cho room 3 (ghế C1 -> C10)
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C1', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C2', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C3', 'VIP');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C4', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C5', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C6', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C7', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C8', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C9', 'STANDARD');
INSERT INTO seat (room_id, seat_number, seat_type) VALUES (3, 'C10', 'STANDARD');
