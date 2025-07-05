package com.nazire.booktrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank(message = "E-posta boş bırakılamaz.")
    @Email(message = "Geçerli bir e posta adresi girin.")
    public String email;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    public String password;


    public String username; // opsiyonel
}
