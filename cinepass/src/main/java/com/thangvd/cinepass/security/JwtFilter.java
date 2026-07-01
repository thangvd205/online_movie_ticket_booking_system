package com.thangvd.cinepass.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;
import java.util.Collections;

import java.util.Arrays;
import java.util.List;


@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.validateAndGetClaims(token);
                String username = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String rolesClaim = claims.get("roles", String.class);
                List<GrantedAuthority> authorities =  StringUtils.hasText(rolesClaim)
                        ? Arrays.stream((rolesClaim.split(",")))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(SimpleGrantedAuthority::new)
                        .map(GrantedAuthority.class::cast)
                        .toList()
                        :List.of();


                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        new JwtUserPrincipal(userId, username), null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ex) {
                // token không hợp lệ hoặc hết hạn, spring security tự chặn nếu endpoint yêu cầu xác thực
            }
        }
        filterChain.doFilter(request, response);
    }
}

