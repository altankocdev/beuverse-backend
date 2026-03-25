package com.altankoc.beuverse_backend.student.service;

import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;
import com.altankoc.beuverse_backend.student.dto.StudentUpdateDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {

    StudentResponseDTO getStudentById(Long id);
    StudentResponseDTO getStudentByUsername(String username);
    StudentResponseDTO updateStudent(Long id, StudentUpdateDTO dto);
    void deleteStudent(Long id);
    Page<StudentResponseDTO> getAllStudents(int page, int size);
    List<StudentSummaryDTO> getDeletedStudents();
    StudentResponseDTO createStudent(RegisterRequestDTO dto);
    Page<StudentResponseDTO> searchStudents(String keyword, int page, int size);
}