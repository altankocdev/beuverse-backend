package com.altankoc.beuverse_backend.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentUpdateDTO(

        @NotBlank(message = "Kullanıcı adı boş bırakılamaz!")
        @Size(min = 3, max = 20, message = "Kullanıcı adı 3 ile 20 karakter arasında olmalıdır!")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Kullanıcı adı yalnızca harf, rakam ve alt çizgi içerebilir!")
        String username,

        @Size(max = 160, message = "Biyografi en fazla 160 karakter olabilir!")
        String bio,

        String profilePhotoUrl
) {
}