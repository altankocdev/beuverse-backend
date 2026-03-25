package com.altankoc.beuverse_backend.auth.service;

import com.altankoc.beuverse_backend.auth.dto.LoginRequestDTO;
import com.altankoc.beuverse_backend.auth.dto.LoginResponseDTO;
import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.core.security.JwtService;
import com.altankoc.beuverse_backend.enums.Department;
import com.altankoc.beuverse_backend.enums.Role;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Student student;
    private RegisterRequestDTO registerDTO;
    private LoginRequestDTO loginWithEmailDTO;
    private LoginRequestDTO loginWithUsernameDTO;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("Altan")
                .lastName("Koç")
                .username("altankoc")
                .email("altan@beun.edu.tr")
                .password("encodedPassword")
                .department(Department.MUHENDISLIK_FAKULTESI)
                .role(Role.STUDENT)
                .emailVerified(true)
                .deleted(false)
                .build();

        registerDTO = new RegisterRequestDTO(
                "Altan",
                "Koç",
                "altankoc",
                "altan@beun.edu.tr",
                "123456",
                Department.MUHENDISLIK_FAKULTESI
        );

        loginWithEmailDTO = new LoginRequestDTO("altan@beun.edu.tr", "123456");
        loginWithUsernameDTO = new LoginRequestDTO("altankoc", "123456");
    }

    // ==================== REGISTER ====================

    @Test
    @DisplayName("Başarılı kayıt")
    void register_ShouldReturnStudentResponseDTO_WhenValidInput() {
        when(studentRepository.existsByEmail(registerDTO.email())).thenReturn(false);
        when(studentRepository.existsByUsername(registerDTO.username())).thenReturn(false);
        when(studentMapper.toEntity(registerDTO)).thenReturn(student);
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encodedPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentMapper.toResponseDTO(student)).thenReturn(
                new StudentResponseDTO(1L, "Altan", "Koç", "altankoc",
                        "altan@beun.edu.tr", null, null,
                        "MUHENDISLIK_FAKULTESI", LocalDateTime.now())
        );

        StudentResponseDTO result = authService.register(registerDTO);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("altan@beun.edu.tr");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Email zaten kullanılıyorsa hata fırlatmalı")
    void register_ShouldThrowBusinessException_WhenEmailAlreadyExists() {
        when(studentRepository.existsByEmail(registerDTO.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu e-posta zaten kullanılıyor!");

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Username zaten kullanılıyorsa hata fırlatmalı")
    void register_ShouldThrowBusinessException_WhenUsernameAlreadyExists() {
        when(studentRepository.existsByEmail(registerDTO.email())).thenReturn(false);
        when(studentRepository.existsByUsername(registerDTO.username())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Bu kullanıcı adı zaten kullanılıyor!");

        verify(studentRepository, never()).save(any());
    }

    // ==================== LOGIN ====================

    @Test
    @DisplayName("Email ile başarılı login")
    void login_ShouldReturnToken_WhenValidEmailAndPassword() {
        when(studentRepository.findByEmail(anyString())).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(student.getEmail())).thenReturn("mockToken");

        LoginResponseDTO result = authService.login(loginWithEmailDTO);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("mockToken");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.student().email()).isEqualTo("altan@beun.edu.tr");
    }

    @Test
    @DisplayName("Username ile başarılı login")
    void login_ShouldReturnToken_WhenValidUsernameAndPassword() {
        when(studentRepository.findByUsername(anyString())).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(student.getEmail())).thenReturn("mockToken");

        LoginResponseDTO result = authService.login(loginWithUsernameDTO);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("mockToken");
    }

    @Test
    @DisplayName("Yanlış şifre ile login hata fırlatmalı")
    void login_ShouldThrowBusinessException_WhenWrongPassword() {
        when(studentRepository.findByEmail(anyString())).thenReturn(Optional.of(student));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginWithEmailDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Şifre hatalı!");
    }

    @Test
    @DisplayName("Olmayan email ile login hata fırlatmalı")
    void login_ShouldThrowResourceNotFoundException_WhenEmailNotFound() {
        when(studentRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginWithEmailDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Öğrenci bulunamadı!");
    }

    @Test
    @DisplayName("Email doğrulanmamışsa login hata fırlatmalı")
    void login_ShouldThrowBusinessException_WhenEmailNotVerified() {
        student.setEmailVerified(false);
        when(studentRepository.findByEmail(anyString())).thenReturn(Optional.of(student));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(loginWithEmailDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-posta adresinizi doğrulamanız gerekmektedir!");
    }
}