package com.altankoc.beuverse_backend.student.controller;

import com.altankoc.beuverse_backend.core.security.SecurityUtils;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import com.altankoc.beuverse_backend.student.dto.StudentSummaryDTO;
import com.altankoc.beuverse_backend.student.dto.StudentUpdateDTO;
import com.altankoc.beuverse_backend.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<Page<StudentResponseDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studentService.getAllStudents(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<StudentResponseDTO> getStudentByUsername(@PathVariable String username) {
        return ResponseEntity.ok(studentService.getStudentByUsername(username));
    }

    @GetMapping("/me")
    public ResponseEntity<StudentResponseDTO> getMe() {
        return ResponseEntity.ok(studentService.getStudentById(SecurityUtils.getCurrentStudentId()));
    }

    @PutMapping("/me")
    public ResponseEntity<StudentResponseDTO> updateMe(@Valid @RequestBody StudentUpdateDTO dto) {
        return ResponseEntity.ok(studentService.updateStudent(SecurityUtils.getCurrentStudentId(), dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe() {
        studentService.deleteStudent(SecurityUtils.getCurrentStudentId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<StudentSummaryDTO>> getDeletedStudents() {
        return ResponseEntity.ok(studentService.getDeletedStudents());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<StudentResponseDTO>> searchStudents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studentService.searchStudents(keyword, page, size));
    }
}