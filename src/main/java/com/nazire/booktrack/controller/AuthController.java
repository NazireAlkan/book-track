package com.nazire.booktrack.controller;

import com.nazire.booktrack.dto.LoginRequest;
import com.nazire.booktrack.dto.LoginResponse;
import com.nazire.booktrack.dto.RegisterRequest;
import com.nazire.booktrack.security.JwtUtil;
import com.nazire.booktrack.service.AuthService;
import com.nazire.booktrack.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService attemptService;

    public AuthController(AuthService authService, JwtUtil jwtUtil, LoginAttemptService attemptService) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.attemptService = attemptService;
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

    @GetMapping("/status")
    public String status(Authentication authentication){
        String email = authentication.getName();

        System.out.println("kullanıcı email:" + email);
        Integer count = attemptService.count(email);
        int kalanHakki = 3 - (count != null ? count : 0); // 3'ten çıkar

        if(kalanHakki <= 0 ){
            System.out.println("Giriş hakkınız doldu!");
        }
        return "Kalan giriş deneme hakkı: " + kalanHakki;
    }

}
