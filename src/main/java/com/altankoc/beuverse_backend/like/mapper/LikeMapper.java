package com.altankoc.beuverse_backend.like.mapper;

import com.altankoc.beuverse_backend.like.dto.LikeResponseDTO;
import com.altankoc.beuverse_backend.like.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "postId", expression = "java(like.getPost() != null ? like.getPost().getId() : null)")
    @Mapping(target = "commentId", expression = "java(like.getComment() != null ? like.getComment().getId() : null)")
    @Mapping(target = "studentId", expression = "java(like.getStudent() != null ? like.getStudent().getId() : null)")
    @Mapping(target = "liked", constant = "true")
    LikeResponseDTO toResponseDTO(Like like);
}