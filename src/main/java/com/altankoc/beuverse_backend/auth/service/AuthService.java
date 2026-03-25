package com.altankoc.beuverse_backend.auth.service;

import com.altankoc.beuverse_backend.auth.dto.LoginRequestDTO;
import com.altankoc.beuverse_backend.auth.dto.LoginResponseDTO;
import com.altankoc.beuverse_backend.auth.dto.RegisterRequestDTO;
import com.altankoc.beuverse_backend.student.dto.StudentResponseDTO;

public interface AuthService {

    StudentResponseDTO register(RegisterRequestDTO dto);
    LoginResponseDTO login(LoginRequestDTO dto);
    void verifyEmail(String token);
}