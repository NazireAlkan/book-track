package com.nazire.booktrack.controller;

import com.nazire.booktrack.dto.LoginRequest;
import com.nazire.booktrack.dto.LoginResponse;
import com.nazire.booktrack.dto.RegisterRequest;
import com.nazire.booktrack.security.JwtUtil;
import com.nazire.booktrack.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return authService.logout(token);
        }
        return "Token bulunamadı.";
    }

    @PostMapping("/refresh")
    public LoginResponse refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String refreshToken = authHeader.substring(7);
            return authService.refreshToken(refreshToken);
        }
        throw new RuntimeException("Refresh token bulunamadı.");
    }
}
