package com.nazire.booktrack.dto;

public class LoginResponse {
    public String token;
    public String tokenType;
    public String email;
    public String username;
    public Long expiresIn; // saniye cinsinden

    public LoginResponse(String token, String email, String username, Long expiresIn) {
        this.token = token;
        this.tokenType = "Bearer";
        this.email = email;
        this.username = username;
        this.expiresIn = expiresIn;
    }
}
