package com.altankoc.beuverse_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @NotBlank(message = "Kullanıcı adı veya e-posta boş bırakılamaz!")
        String usernameOrEmail,

        @NotBlank(message = "Şifre alanı boş bırakılamaz!")
        String password
) {
}