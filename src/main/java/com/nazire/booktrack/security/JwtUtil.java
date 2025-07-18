package com.nazire.booktrack.security;

import com.nazire.booktrack.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

//Bu sınıf JWT token üretmek ve doğrulamak için.
@Component
public class JwtUtil {

    private static final String SECRET = "nazireilegucluolanzincirikirsarjetburda";
    
    // Token süreleri
    private static final long ACCESS_TOKEN_VALIDITY =  60 * 10 * 1000; // 10 dakika
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 gün

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final TokenBlacklistService tokenBlacklistService;

    public JwtUtil(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // Access Token üretir (5 dakika)
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .claim("type", "access")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token üretir (7 gün)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .claim("type", "refresh")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Token'dan kullanıcı adını (email) çıkarır
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Token'dan expiration date'i çıkarır
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Token tipini kontrol eder
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    // Token'dan belirli bir claim'i çıkarır
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Token'dan tüm claim'leri çıkarır
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Token'ın süresinin dolup dolmadığını kontrol eder
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Access Token'ı doğrular
    public Boolean validateAccessToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final String tokenType = extractTokenType(token);
        return (username.equals(userDetails.getUsername()) 
                && !isTokenExpired(token) 
                && "access".equals(tokenType)
                && !tokenBlacklistService.isTokenBlacklisted(token));
    }

    // Refresh Token'ı doğrular
    public Boolean validateRefreshToken(String token) {
        final String tokenType = extractTokenType(token);//refresh ok
        return (!isTokenExpired(token) // true
                && "refresh".equals(tokenType)
                && !tokenBlacklistService.isTokenBlacklisted(token));
    }

    // Access token süresi getter'ı
    public long getAccessTokenValidityInSeconds() {
        return ACCESS_TOKEN_VALIDITY / 1000;
    }

    // Refresh token süresi getter'ı
    public long getRefreshTokenValidityInSeconds() {
        return REFRESH_TOKEN_VALIDITY / 1000;
    }
}
