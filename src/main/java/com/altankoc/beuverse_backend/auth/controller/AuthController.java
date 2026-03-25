package com.altankoc.beuverse_backend.auth.controller;

import com.altankoc.beuverse_backend.auth.dto.LoginRequestDTO;
import com.altankoc.beuverse_backend.auth.dto.LoginResponseDTO;
import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.auth.service.AuthService;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StudentResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("E-posta adresiniz başarıyla doğrulandı!");
    }
}