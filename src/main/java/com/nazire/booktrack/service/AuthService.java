package com.nazire.booktrack.service;

import com.nazire.booktrack.dto.LoginRequest;
import com.nazire.booktrack.dto.LoginResponse;
import com.nazire.booktrack.dto.RegisterRequest;
import com.nazire.booktrack.model.User;
import com.nazire.booktrack.repository.UserRepository;
import com.nazire.booktrack.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request){
        if(userRepository.findByEmail(request.email).isPresent()){
            throw new RuntimeException("Bu e-posta zaten kayıtlı!");
        }

        User user = new User(
                request.email,
                passwordEncoder.encode(request.password),
                request.username != null ? request.username : ""
        );
        userRepository.save(user);
        return "Hoşgeldin, " + (user.getUsername().isEmpty() ? "kullanıcı" : user.getUsername()) + "!";
    }

    public LoginResponse login(LoginRequest request){
        var user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("E posta bulunamadı."));

        if(!passwordEncoder.matches(request.password, user.getPassword())){
            throw new RuntimeException("Şifre hatalı.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        
        return new LoginResponse(
            token,
            user.getEmail(),
            user.getUsername(),
            86400L // 1 gün = 86400 saniye
        );
    }
}
