package com.altankoc.beuverse_backend.comment.mapper;

import com.altankoc.beuverse_backend.comment.dto.CommentRequestDTO;
import com.altankoc.beuverse_backend.comment.dto.CommentResponseDTO;
import com.altankoc.beuverse_backend.comment.entity.Comment;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "likes", ignore = true)
    Comment toEntity(CommentRequestDTO dto);

    @Mapping(target = "student", source = "student")
    @Mapping(target = "parentCommentId", expression = "java(comment.getParentComment() != null ? comment.getParentComment().getId() : null)")
    @Mapping(target = "postId", expression = "java(comment.getPost() != null ? comment.getPost().getId() : null)")
    @Mapping(target = "postOwnerUsername", expression = "java(comment.getPost() != null && comment.getPost().getStudent() != null ? comment.getPost().getStudent().getUsername() : null)")
    @Mapping(target = "isLiked", constant = "false")
    CommentResponseDTO toResponseDTO(Comment comment);


}