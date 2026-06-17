package com.thangvd.cinepass.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SeatAlreadyBookedException.class)
    public ResponseEntity<Map<String, String>> handleSeatAlreadyBooked(SeatAlreadyBookedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.clear();;
        errors.put("Error", "Đặt ghế bị trùng");
        errors.put("message", ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

}
