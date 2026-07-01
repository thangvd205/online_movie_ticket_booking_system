package com.thangvd.cinepass.service;

import com.thangvd.cinepass.model.AppUser;
import com.thangvd.cinepass.repository.UserRepository;
import com.thangvd.cinepass.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AppUser register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new com.thangvd.cinepass.exception.DuplicateUsernameException("Tên người dùng đã tồn tại");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles("ROLE_USER");
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        return jwtUtil.generateToken(username, user.getId(), user.getRoles());
    }
}