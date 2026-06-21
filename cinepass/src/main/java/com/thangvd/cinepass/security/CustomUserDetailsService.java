package com.thangvd.cinepass.security;

import com.thangvd.cinepass.model.AppUser;
import com.thangvd.cinepass.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        List<GrantedAuthority> authorities = Arrays.stream(appUser.getRoles().split(","))
                .map((role -> (GrantedAuthority) new SimpleGrantedAuthority(role))).toList();
        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }
}

