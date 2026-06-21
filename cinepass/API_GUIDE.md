# CinePass API Guide - Hướng dẫn sử dụng API

## 1. Booking vé (POST)

### Endpoint
```
POST http://localhost:8080/api/tickets/book
```

### Request Body (JSON)
```json
{
  "showtimeId": 1,
  "seatId": 1,
  "price": 150000.0,
  "userId": 1
}
```

### Response (201 Created)
```json
{
  "id": 1,
  "bookingCode": null,
  "movieTitle": "Phim Bom Tấn CinePass 2026",
  "cinemaName": "Beta Cinemas Thanh Xuân",
  "cinemaAddress": "Tầng hầm B1, tòa nhà Golden West, 2 phường Lê Văn Thiên, Thanh Xuân, Hà Nội",
  "roomName": "Phòng Chiếu 01 (IMAX)",
  "seatNumber": "A1",
  "price": 150000.0,
  "bookingTime": "2026-06-21T16:59:52",
  "startTime": "2026-06-22T19:00:00",
  "status": "HOLDING",
  "expiryTime": "2026-06-21T17:14:52"
}
```

**Ghi chú:**
- `status: HOLDING` = Vé đang trong trạng thái chờ thanh toán
- `expiryTime` = Thời gian hết hạn (15 phút sau booking)
- `bookingCode` = null (sẽ có khi confirm payment)

---

## 2. Confirm Payment - Xác nhận thanh toán (POST)

### Endpoint
```
POST http://localhost:8080/api/tickets/{id}/confirm
```

### Path Parameter
- `id` = Ticket ID (lấy từ kết quả booking)

### Example
```
POST http://localhost:8080/api/tickets/1/confirm
```

### Response (200 OK)
```json
{
  "id": 1,
  "bookingCode": "CT-ABC123",
  "movieTitle": "Phim Bom Tấn CinePass 2026",
  "cinemaName": "Beta Cinemas Thanh Xuân",
  "cinemaAddress": "Tầng hầm B1, tòa nhà Golden West, 2 phường Lê Văn Thiên, Thanh Xuân, Hà Nội",
  "roomName": "Phòng Chiếu 01 (IMAX)",
  "seatNumber": "A1",
  "price": 150000.0,
  "bookingTime": "2026-06-21T16:59:52",
  "startTime": "2026-06-22T19:00:00",
  "status": "CONFIRMED",
  "expiryTime": "2026-06-21T17:14:52"
}
```

**Ghi chú:**
- `status: CONFIRMED` = Thanh toán thành công
- `bookingCode: CT-ABC123` = Mã vé điện tử (có thể dùng để in vé)

---

## 3. Xem sơ đồ ghế (GET)

### Endpoint
```
GET http://localhost:8080/api/showtimes/{showtimeId}/seats
```

### Example
```
GET http://localhost:8080/api/showtimes/1/seats
```

### Response (200 OK)
```json
[
  {
    "seatId": 1,
    "seatNumber": "A1",
    "seatType": "VIP",
    "status": "HOLDING"
  },
  {
    "seatId": 2,
    "seatNumber": "A2",
    "seatType": "VIP",
    "status": "AVAILABLE"
  },
  {
    "seatId": 3,
    "seatNumber": "A3",
    "seatType": "STANDARD",
    "status": "CONFIRMED"
  }
]
```

**Trạng thái ghế:**
- `AVAILABLE` = Ghế trống (màu sáng - có thể đặt)
- `HOLDING` = Ghế đang chờ thanh toán (màu vàng - 15 phút)
- `CONFIRMED` = Ghế đã thanh toán (màu tối - không thể đặt)

---

## 4. Lịch sử vé (GET)

### Endpoint
```
GET http://localhost:8080/api/tickets/history?userId={userId}
```

### Example
```
GET http://localhost:8080/api/tickets/history?userId=1
```

### Response (200 OK)
```json
[
  {
    "id": 1,
    "bookingCode": "CT-ABC123",
    "movieTitle": "Phim Bom Tấn CinePass 2026",
    "cinemaName": "Beta Cinemas Thanh Xuân",
    "cinemaAddress": "Tầng hầm B1, tòa nhà Golden West, 2 phường Lê Văn Thiên, Thanh Xuân, Hà Nội",
    "roomName": "Phòng Chiếu 01 (IMAX)",
    "seatNumber": "A1",
    "price": 150000.0,
    "bookingTime": "2026-06-21T16:59:52",
    "startTime": "2026-06-22T19:00:00",
    "status": "CONFIRMED",
    "expiryTime": "2026-06-21T17:14:52"
  }
]
```

---

## Test Flow - Luồng test đầy đủ

### Step 1: Booking vé
```
POST http://localhost:8080/api/tickets/book

{
  "showtimeId": 1,
  "seatId": 1,
  "price": 150000.0,
  "userId": 1
}
```
✅ Lấy `id` = 1 từ response

### Step 2: Xem sơ đồ ghế (optional)
```
GET http://localhost:8080/api/showtimes/1/seats
```
✅ Kiểm tra ghế A1 có status = "HOLDING"

### Step 3: Confirm Payment
```
POST http://localhost:8080/api/tickets/1/confirm
```
✅ Kiểm tra status = "CONFIRMED" và có bookingCode

### Step 4: Kiểm tra lại sơ đồ ghế
```
GET http://localhost:8080/api/showtimes/1/seats
```
✅ Ghế A1 bây giờ có status = "CONFIRMED"

### Step 5: Xem lịch sử vé
```
GET http://localhost:8080/api/tickets/history?userId=1
```
✅ Thấy vé vừa confirm trong danh sách

---

## Error Codes

| Status | Error Message | Nguyên nhân |
|--------|---------------|-----------|
| 400 | "Suất chiếu không hợp lệ!" | showtimeId không tồn tại |
| 400 | "Ghế không hợp lệ!" | seatId không tồn tại |
| 400 | "Ghế đã bị đặt, vui lòng chọn ghế khác!" | Ghế đã được booking bởi user khác |
| 400 | "Không tìm thấy vé đã đặt!" | ticketId không tồn tại |
| 400 | "Thời gian đặt vé đã hết, vui lòng thực hiện lại!" | Vé đã hết hạn 15 phút |


