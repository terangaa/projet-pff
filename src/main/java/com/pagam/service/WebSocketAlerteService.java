package com.pagam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketAlerteService {

    private final SimpMessagingTemplate messagingTemplate;

    // Méthode pour envoyer l'alerte à tous les clients connectés
    public void envoyerAlerte(Map<String, Object> alerte) {
        messagingTemplate.convertAndSend("/topic/alertes", alerte);
    }
}
