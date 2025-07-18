package com.nazire.booktrack.dto;

public class LoginResponse {
    public String accessToken;
    public String refreshToken;
    public String tokenType;
    public String email;
    public String username;
    public Long accessTokenExpiresIn; // access token süresi (saniye)
    public Long refreshTokenExpiresIn; // refresh token süresi (saniye)

    public LoginResponse(String accessToken, String refreshToken, String email, String username, 
                        Long accessTokenExpiresIn, Long refreshTokenExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.email = email;
        this.username = username;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}
