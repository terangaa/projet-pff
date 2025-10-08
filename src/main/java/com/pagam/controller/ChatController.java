package com.pagam.controller;

import com.pagam.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Enregistrer utilisateur en ligne
    @MessageMapping("/chat/join")
    public void join(ChatMessage message) {
        connectedUsers.add(message.getUsername());
        messagingTemplate.convertAndSend("/topic/users", connectedUsers);
    }

    // Message global
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }

    // Message privé
    @MessageMapping("/chat/private")
    public void sendPrivate(ChatMessage message) {
        if(message.getReceiver() != null) {
            messagingTemplate.convertAndSendToUser(
                    message.getReceiver(), "/queue/messages", message);
        }
    }

    // Déconnexion
    @MessageMapping("/chat/leave")
    public void leave(ChatMessage message) {
        connectedUsers.remove(message.getUsername());
        messagingTemplate.convertAndSend("/topic/users", connectedUsers);
    }
}
