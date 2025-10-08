package com.pagam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/decideur")
public class DecideurController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("email", "decideur@exemple.com");
        return "dashboard/decideur-home";
    }

    @GetMapping("/statistiques")
    public String statistiques() {
        return "dashboard/statistiques";
    }
}
