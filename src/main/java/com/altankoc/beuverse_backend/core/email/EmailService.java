package com.altankoc.beuverse_backend.core.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String toEmail, String firstName, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("altancode@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Beuverse — E-posta Adresinizi Doğrulayın");

            String verificationUrl = "https://api.beuverse.fatihaktas.xyz/api/v1/auth/verify-email?token=" + verificationToken;

            String htmlContent = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                        <h2 style="color: #27374D;">Merhaba %s,</h2>
                        <p>Beuverse'e hoş geldiniz!</p>
                        <p>Hesabınızı aktifleştirmek için aşağıdaki bağlantıya tıklayın:</p>
                        <a href="%s"
                           style="display: inline-block; padding: 12px 24px; background-color: #526D82;
                                  color: white; text-decoration: none; border-radius: 6px; margin: 16px 0;">
                            E-postamı Doğrula
                        </a>
                        <p style="color: #888;">Bu bağlantı 24 saat geçerlidir.</p>
                        <p style="color: #888;">Eğer bu hesabı siz oluşturmadıysanız bu e-postayı dikkate almayınız.</p>
                        <br>
                        <p style="color: #526D82;">Beuverse Ekibi</p>
                    </div>
                    """.formatted(firstName, verificationUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Doğrulama maili gönderildi: {}", toEmail);

        } catch (Exception e) {
            log.error("Mail gönderilemedi: {}", e.getMessage());
        }
    }
}