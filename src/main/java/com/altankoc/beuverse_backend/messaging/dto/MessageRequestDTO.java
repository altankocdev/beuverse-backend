package com.altankoc.beuverse_backend.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequestDTO(
        @NotBlank(message = "Mesaj boş bırakılamaz!")
        @Size(max = 1000, message = "Mesaj en fazla 1000 karakter olabilir!")
        String content
) {
}