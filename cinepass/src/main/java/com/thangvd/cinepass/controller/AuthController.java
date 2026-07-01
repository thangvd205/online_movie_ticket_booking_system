package com.thangvd.cinepass.controller;

import com.thangvd.cinepass.dto.AuthRequest;
import com.thangvd.cinepass.dto.AuthResponse;
import com.thangvd.cinepass.model.AppUser;
import com.thangvd.cinepass.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest req) {
        AppUser user = authService.register(req.getUsername(), req.getPassword());
        return ResponseEntity.ok().body("Đăng ký thành công người dùng: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        String token = authService.login(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}