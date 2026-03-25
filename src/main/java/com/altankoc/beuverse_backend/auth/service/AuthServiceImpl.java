package com.altankoc.beuverse_backend.auth.service;

import com.altankoc.beuverse_backend.auth.dto.LoginRequestDTO;
import com.altankoc.beuverse_backend.auth.dto.LoginResponseDTO;
import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.core.security.JwtService;
import com.altankoc.beuverse_backend.core.exception.BusinessException;
import com.altankoc.beuverse_backend.core.exception.ResourceNotFoundException;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import com.altankoc.beuverse_backend.student.entity.Student;
import com.altankoc.beuverse_backend.student.mapper.StudentMapper;
import com.altankoc.beuverse_backend.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public StudentResponseDTO register(RegisterRequestDTO dto) {
        if (studentRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Bu e-posta zaten kullanılıyor!");
        }
        if (studentRepository.existsByUsername(dto.username())) {
            throw new BusinessException("Bu kullanıcı adı zaten kullanılıyor!");
        }

        Student student = studentMapper.toEntity(dto);
        student.setPassword(passwordEncoder.encode(dto.password()));
        student.setEmailVerified(true);

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Student student = dto.usernameOrEmail().contains("@")
                ? studentRepository.findByEmail(dto.usernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"))
                : studentRepository.findByUsername(dto.usernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Öğrenci bulunamadı!"));

        if (!passwordEncoder.matches(dto.password(), student.getPassword())) {
            throw new BusinessException("Şifre hatalı!");
        }

        if (!student.isEmailVerified()) {
            throw new BusinessException("E-posta adresinizi doğrulamanız gerekmektedir!");
        }

        String token = jwtService.generateToken(student.getEmail());

        return new LoginResponseDTO(
                token,
                "Bearer",
                new LoginResponseDTO.StudentInfoDTO(
                        student.getId(),
                        student.getUsername(),
                        student.getEmail(),
                        student.getRole().name()
                )
        );
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        Student student = studentRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Geçersiz doğrulama token'ı!"));

        student.setEmailVerified(true);
        student.setEmailVerificationToken(null);
        studentRepository.save(student);
    }
}