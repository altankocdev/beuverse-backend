package com.altankoc.beuverse_backend.student.service;

import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;
import com.altankoc.beuverse_backend.student.dto.StudentUpdateDTO;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));
        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByUsername(String username) {
        Student student = studentRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));
        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentUpdateDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        if (!student.getUsername().equals(dto.username()) &&
                studentRepository.existsByUsername(dto.username())) {
            throw new BusinessException("Bu kullanıcı adı zaten kullanılıyor!");
        }

        studentMapper.updateStudentFromDTO(dto, student);
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));
        student.setDeleted(true);
        studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findAllByDeletedFalse(pageable)
                .map(studentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentSummaryDTO> getDeletedStudents() {
        return studentRepository.findAllByDeletedTrue()
                .stream()
                .map(studentMapper::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional
    public StudentResponseDTO createStudent(RegisterRequestDTO dto) {
        if (studentRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Bu e-posta zaten kullanılıyor!");
        }
        if (studentRepository.existsByUsername(dto.username())) {
            throw new BusinessException("Bu kullanıcı adı zaten kullanılıyor!");
        }

        Student student = studentMapper.toEntity(dto);
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudents(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository
                .findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        keyword, keyword, keyword, pageable)
                .map(studentMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateProfilePhoto(Long id, String photoUrl) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));
        student.setProfilePhotoUrl(photoUrl);
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }
}