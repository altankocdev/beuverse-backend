package com.altankoc.beuverse_backend.student.mapper;

import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;
import com.altankoc.beuverse_backend.student.dto.StudentUpdateDTO;
import com.altankoc.beuverse_backend.student.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "department", expression = "java(student.getDepartment().name())")
    StudentResponseDTO toResponseDTO(Student student);

    StudentSummaryDTO toSummaryDTO(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    void updateStudentFromDTO(StudentUpdateDTO dto, @MappingTarget Student student);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "profilePhotoUrl", ignore = true)
    Student toEntity(RegisterRequestDTO dto);
}