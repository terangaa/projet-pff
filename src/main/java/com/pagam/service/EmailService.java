package com.pagam.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void envoyerAlerteEmail(String to, String sujet, String contenuHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(sujet);
            helper.setText(contenuHtml, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Le nouveau mail admin avec les liens Accepter / Refuser
    public void envoyerMailAdmin(Long demandeId, String nomProduit, String messageUtilisateur) {
        String adminEmail = "sadikhyade851@gmail.com";
        String sujet = "🆕 Nouvelle demande d'ajout de produit";

        String urlAccepter = "http://localhost:8086/produits/demande/accepter/" + demandeId;
        String urlRefuser   = "http://localhost:8086/produits/demande/refuser/" + demandeId;

        String contenu = "<!DOCTYPE html>"
                + "<html lang='fr'>"
                + "<head><meta charset='UTF-8'>"
                + "<style>"
                + "body { font-family: Arial; background-color: #f4f4f4; padding: 20px; }"
                + ".card { background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }"
                + "h3 { color: #0d6efd; }"
                + "p { line-height: 1.5; }"
                + ".btn { display:inline-block;padding:10px 20px;border-radius:5px;color:#fff;text-decoration:none;margin:5px; }"
                + ".btn-accept { background-color:#28a745; }"
                + ".btn-decline { background-color:#dc3545; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='card'>"
                + "<h3>Nouvelle demande d'ajout de produit</h3>"
                + "<p><strong>Produit demandé :</strong> " + nomProduit + "</p>"
                + "<p><strong>Message :</strong> " + (messageUtilisateur != null ? messageUtilisateur : "Pas de message") + "</p>"
                + "<div style='margin-top:20px;'>"
                + "<a href='" + urlAccepter + "' class='btn btn-accept'>✅ Accepter</a>"
                + "<a href='" + urlRefuser + "' class='btn btn-decline'>❌ Refuser</a>"
                + "</div>"
                + "</div>"
                + "<p style='font-size:0.9em;color:#555;margin-top:20px;'>Ceci est un mail automatique.</p>"
                + "</body></html>";

        envoyerAlerteEmail(adminEmail, sujet, contenu);
    }
}
