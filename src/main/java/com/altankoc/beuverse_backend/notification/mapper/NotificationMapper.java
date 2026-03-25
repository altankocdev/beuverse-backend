package com.altankoc.beuverse_backend.notification.mapper;

import com.altankoc.beuverse_backend.notification.dto.NotificationResponseDTO;
import com.altankoc.beuverse_backend.notification.entity.Notification;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface NotificationMapper {

    @Mapping(target = "sender", source = "senderStudent")
    NotificationResponseDTO toResponseDTO(Notification notification);
}