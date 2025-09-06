package com.pagam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void envoyerAlerte(String message) {
        messagingTemplate.convertAndSend("/topic/alerts", message);
    }
}
