package com.pagam.controller;

import com.pagam.entity.MessageContact;
import com.pagam.repository.MessageContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageContactController {

    @Autowired
    private MessageContactRepository messageContactRepository;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/contact/send")
    public String envoyerMessage(@RequestParam String nom,
                                 @RequestParam String email,
                                 @RequestParam String message,
                                 Model model) {

        // Sauvegarder le message dans la base
        MessageContact mc = new MessageContact();
        mc.setNom(nom);
        mc.setEmail(email);
        mc.setMessage(message);
        messageContactRepository.save(mc);

        // ✅ Envoyer le mail automatiquement à l'administrateur
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("sadikhyade851@gmail.com","insam621.com@gmail.com","papesy302001@gmail.com"); // <-- mets ton email ici
            mailMessage.setSubject("📩 Nouveau message de contact - PAGAM");
            mailMessage.setText(
                    "Vous avez reçu un nouveau message depuis le formulaire de contact :\n\n" +
                            "👤 Nom : " + nom + "\n" +
                            "📧 Email : " + email + "\n\n" +
                            "💬 Message : \n" + message
            );
            mailSender.send(mailMessage);

            model.addAttribute("success", "✅ Merci " + nom + ", votre message a été envoyé avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "❌ Erreur lors de l’envoi de votre message. Veuillez réessayer.");
            e.printStackTrace();
        }

        return "homes/contact";
    }
}
