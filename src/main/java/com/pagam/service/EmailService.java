package com.pagam.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    public void envoyerAlerteEmail(String to, String sujet, String contenuHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(sujet);
            helper.setText(contenuHtml, true); // true => HTML

            mailSender.send(message);
            System.out.println("✅ Email envoyé à : " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur d'envoi mail : " + e.getMessage());
        }
    }
}
