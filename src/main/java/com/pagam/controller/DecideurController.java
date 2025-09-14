package com.pagam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decideur")
public class DecideurController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Bienvenue sur le tableau de bord DECIDEUR 📊";
    }
}
