package com.pagam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {

    // Accessible via http://localhost:8086/chat
    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // Renvoie chat.html qui doit être dans templates
    }
}
