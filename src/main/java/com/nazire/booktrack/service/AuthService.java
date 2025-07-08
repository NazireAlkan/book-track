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
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
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

        // Eğer kullanıcının eski token'ı varsa blacklist'e ekle
        if (user.getLastToken() != null && !user.getLastToken().isEmpty()) {
            tokenBlacklistService.blacklistToken(user.getLastToken());
        }

        // Yeni token oluştur
        String token = jwtUtil.generateToken(user.getEmail());
        
        // Yeni token'ı kullanıcıya kaydet
        user.setLastToken(token);
        userRepository.save(user);
        
        return new LoginResponse(
            token,
            user.getEmail(),
            user.getUsername(),
            86400L // 1 gün = 86400 saniye
        );
    }

    // Logout işlemi - token'ı blacklist'e ekler
    public String logout(String token) {
        tokenBlacklistService.blacklistToken(token);
        
        // Token'dan email çıkar ve kullanıcının lastToken'ını temizle
        String email = jwtUtil.extractUsername(token);
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            user.get().setLastToken(null);
            userRepository.save(user.get());
        }
        
        return "Başarıyla çıkış yapıldı.";
    }

    // Yeni token alırken eski token'ı geçersiz kılar
    public LoginResponse refreshToken(String oldToken, String email) {
        // Eski token'ı blacklist'e ekle
        tokenBlacklistService.blacklistToken(oldToken);
        
        // Yeni token oluştur
        String newToken = jwtUtil.generateToken(email);
        
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        
        return new LoginResponse(
            newToken,
            user.getEmail(),
            user.getUsername(),
            86400L
        );
    }
}
