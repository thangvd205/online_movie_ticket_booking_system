package com.thangvd.cinepass.dto;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private String status;
    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(String status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

//    hàm tiện ích trả về nhanh khi thành công
    public static <T> ApiResponse success(T data, String message) {
        return new ApiResponse<>("success", 200, message, data);
    }

//    hàm tiện ích để trả về nhanh khi lỗi
    public static <T> ApiResponse error(int code, String message) {
        return new ApiResponse<>("error", code, message, null);
    }

//    khởi tạo getter/setter

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
