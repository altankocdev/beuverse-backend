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
        String html = """
            <!DOCTYPE html>
            <html lang="tr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Beuverse — E-posta Doğrulandı</title>
                <style>
                    body { font-family: Arial, sans-serif; display: flex; justify-content: center;
                           align-items: center; min-height: 100vh; margin: 0;
                           background-color: #f0f0f8; }
                    .card { background: white; padding: 48px; border-radius: 16px;
                            text-align: center; max-width: 480px; box-shadow: 0 4px 24px rgba(0,0,0,0.1); }
                    .icon { font-size: 64px; margin-bottom: 24px; }
                    h1 { color: #27374D; margin-bottom: 12px; font-size: 24px; }
                    p { color: #526D82; line-height: 1.6; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="icon">✅</div>
                    <h1>E-posta Doğrulandı!</h1>
                    <p>Hesabınız başarıyla aktifleştirildi.<br>
                    Artık Beuverse uygulamasından giriş yapabilirsiniz.</p>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }
}