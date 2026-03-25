package com.altankoc.beuverse_backend.post.mapper;

import com.altankoc.beuverse_backend.post.dto.PostRequestDTO;
import com.altankoc.beuverse_backend.post.dto.PostResponseDTO;
import com.altankoc.beuverse_backend.post.entity.Post;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    Post toEntity(PostRequestDTO dto);

    @Mapping(target = "tag", expression = "java(post.getTag().name())")
    @Mapping(target = "student", source = "post.student")
    @Mapping(target = "imageUrls", expression = "java(post.getImages().stream().map(img -> img.getImageUrl()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "isLiked", source = "isLiked")
    PostResponseDTO toResponseDTO(Post post, boolean isLiked);
}