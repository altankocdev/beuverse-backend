package com.altankoc.beuverse_backend.post.dto;

import com.altankoc.beuverse_backend.enums.PostTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequestDTO(

        @NotBlank(message = "İçerik boş bırakılamaz!")
        @Size(min = 1, max = 500, message = "İçerik en fazla 500 karakter olabilir!")
        String content,

        @NotNull(message = "Etiket boş bırakılamaz!")
        PostTag tag,

        List<String> imageUrls
) {
}