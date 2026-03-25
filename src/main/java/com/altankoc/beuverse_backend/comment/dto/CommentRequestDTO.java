package com.altankoc.beuverse_backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(

        @NotNull(message = "Post ID boş bırakılamaz!")
        Long postId,

        @Size(max = 300, message = "Yorum en fazla 300 karakter olabilir!")
        @NotBlank(message = "Yorum boş bırakılamaz!")
        String content,

        Long parentCommentId
) {
}