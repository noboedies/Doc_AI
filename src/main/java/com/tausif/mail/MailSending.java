package com.tausif.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tausif.jwt.JwtUtil;

@Service
public class MailSending {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtUtil jwtUtil;

    /* ===============================
       TEXT MAIL (fallback / testing)
       =============================== */
    public void sendResetPasswordTextMail(String email) {

        String subject = "Password Reset Request";
        String token = jwtUtil.generateToken(email);
        String resetLink =
                "https://docai-production-37c8.up.railway.app/reset_password?token=" + token;

        String body =
                "We received a request to reset your password.\n\n" +
                "Reset your password using the link below:\n" +
                resetLink + "\n\n" +
                "This link is valid for 15 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "— Doc_AI Security Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /* ===============================
       HTML MAIL (PRODUCTION)
       =============================== */
    public void sendResetPasswordHtmlMail(String email) {

        try {
            String subject = "Password Reset Request";
            String token = jwtUtil.generateToken(email);

            String resetLink =
                    "https://docai-production-37c8.up.railway.app/reset_password?token=" + token;

            String htmlBody = """
                <div style="font-family:Arial, sans-serif; line-height:1.6;">
                    <h2 style="color:#2c3e50;">Password Reset Request</h2>

                    <p>We received a request to reset your password.</p>

                    <p>
                        <a href="%s"
                           style="display:inline-block;
                                  padding:12px 20px;
                                  background-color:#3498db;
                                  color:#ffffff;
                                  text-decoration:none;
                                  border-radius:6px;">
                            Reset Password
                        </a>
                    </p>

                    <p><b>This link is valid for 15 minutes.</b></p>

                    <p>
                        If you did not request a password reset,
                        you can safely ignore this email.
                    </p>

                    <br>
                    <p>Thanks,<br><b>Doc_AI Security Team</b></p>
                </div>
                """.formatted(resetLink);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // ✅ HTML enabled

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send reset password email", e);
        }
    }
}
