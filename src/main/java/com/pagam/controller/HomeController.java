package com.pagam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Redirection depuis la racine
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    // Page d'accueil publique
    @GetMapping("/home")
    public String home() {
        return "home"; // Nom du template home.html
    }
}
