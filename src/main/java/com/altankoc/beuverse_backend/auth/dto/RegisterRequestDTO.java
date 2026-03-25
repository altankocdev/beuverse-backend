package com.altankoc.beuverse_backend.auth.dto;

import com.altankoc.beuverse_backend.enums.Department;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(

        @NotBlank(message = "Ad alanı boş bırakılamaz!")
        @Size(min = 2, max = 30, message = "Ad 2 ile 30 karakter arasında olmalıdır!")
        String firstName,

        @NotBlank(message = "Soyad alanı boş bırakılamaz!")
        @Size(min = 2, max = 30, message = "Soyad 2 ile 30 karakter arasında olmalıdır!")
        String lastName,

        @NotBlank(message = "Kullanıcı adı boş bırakılamaz!")
        @Size(min = 3, max = 20, message = "Kullanıcı adı 3 ile 20 karakter arasında olmalıdır!")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Kullanıcı adı yalnızca harf, rakam ve alt çizgi içerebilir!")
        String username,

        @NotBlank(message = "E-posta alanı boş bırakılamaz!")
        @Email(message = "Geçerli bir e-posta adresi giriniz!")
        String email,

        @NotBlank(message = "Şifre alanı boş bırakılamaz!")
        @Size(min = 6, max = 20, message = "Şifre 6 ile 20 karakter arasında olmalıdır!")
        String password,

        @NotNull(message = "Bölüm alanı boş bırakılamaz!")
        Department department
) {
}