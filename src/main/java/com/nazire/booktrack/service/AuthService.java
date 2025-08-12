package com.nazire.booktrack.service;

import com.nazire.booktrack.dto.LoginRequest;
import com.nazire.booktrack.dto.LoginResponse;
import com.nazire.booktrack.dto.RegisterRequest;
import com.nazire.booktrack.model.Role;
import com.nazire.booktrack.model.User;
import com.nazire.booktrack.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService, RoleRepository roleRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
        this.roleRepository = roleRepository;
        this.loginAttemptService = loginAttemptService;
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

        Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("ROLE_USER bulunamadı"));
        user.getRoles().add(userRole);

        userRepository.save(user);
        return "Hoşgeldin, " + (user.getUsername().isEmpty() ? "kullanıcı" : user.getUsername()) + "!";
    }

    public LoginResponse login(LoginRequest request){
        // 3 kere denme sınırı
        if(loginAttemptService.isBlocked(request.email)){
            throw new RuntimeException("Deneme sınırı aşıldı!");
        }

        var user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("E posta bulunamadı."));

        if(!passwordEncoder.matches(request.password, user.getPassword())){
            System.out.println("girdi1");
            loginAttemptService.loginFailed(request.email); // sayacı arttır
            throw new RuntimeException("Şifre hatalı");
        }

        loginAttemptService.loginSucceeded(request.email); // başarılıysa value'yi sıfırla

        // Eğer kullanıcının eski token'ları varsa blacklist'e ekle
        if (user.getLastToken() != null && !user.getLastToken().isEmpty()) {
            tokenBlacklistService.blacklistToken(user.getLastToken());
        }
        if (user.getRefreshToken() != null && !user.getRefreshToken().isEmpty()) {
            tokenBlacklistService.blacklistToken(user.getRefreshToken());
        }

        // Yeni token'ları oluştur
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        // Yeni token'ları kullanıcıya kaydet
        user.setLastToken(accessToken);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(
            accessToken,
            refreshToken,
            user.getEmail(),
            user.getUsername(),
            jwtUtil.getAccessTokenValidityInSeconds(),
            jwtUtil.getRefreshTokenValidityInSeconds()
        );
    }

    // Logout işlemi - token'ları blacklist'e ekler
    public String logout(String token) {
        tokenBlacklistService.blacklistToken(token);
        
        // Token'dan email çıkar ve kullanıcının token'larını temizle
        String email = jwtUtil.extractUsername(token);
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            // Refresh token'ı da blacklist'e ekle
            if (user.get().getRefreshToken() != null) {
                tokenBlacklistService.blacklistToken(user.get().getRefreshToken());
            }
            user.get().setLastToken(null);
            user.get().setRefreshToken(null);
            userRepository.save(user.get());
        }
        
        return "Başarıyla çıkış yapıldı.";
    }

    // Refresh token ile yeni access token al
    public LoginResponse refreshToken(String refreshToken) {
        // Refresh token'ı doğrula
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Geçersiz refresh token.");
        }
        
        // Refresh token'dan email çıkar
        String email = jwtUtil.extractUsername(refreshToken);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        
        // Kullanıcının kaydedilmiş refresh token'ı ile gelen token'ı karşılaştır
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("Refresh token eşleşmiyor.");
        }
        
        // Eski access token'ı blacklist'e ekle (eğer varsa)
        if (user.getLastToken() != null && !user.getLastToken().isEmpty()) {
            tokenBlacklistService.blacklistToken(user.getLastToken());
        }
        
        // Yeni access token oluştur
        String newAccessToken = jwtUtil.generateAccessToken(email);
        
        // Yeni access token'ı kaydet
        user.setLastToken(newAccessToken);
        userRepository.save(user);
        
        return new LoginResponse(
            newAccessToken,
            refreshToken, // Refresh token aynı kalır
            user.getEmail(),
            user.getUsername(),
            jwtUtil.getAccessTokenValidityInSeconds(),
            jwtUtil.getRefreshTokenValidityInSeconds()
        );
    }
}
