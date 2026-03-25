package com.altankoc.beuverse_backend.messaging.mapper;

import com.altankoc.beuverse_backend.messaging.dto.MessageResponseDTO;
import com.altankoc.beuverse_backend.messaging.entity.Message;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface MessagingMapper {

    @Mapping(target = "sender", source = "sender")
    MessageResponseDTO toMessageResponseDTO(Message message);
}