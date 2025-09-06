package com.pagam.controller;

import com.pagam.entity.Alerte;
import com.pagam.service.AlerteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AlerteController {

    private final AlerteService alerteService;

    public AlerteController(AlerteService alerteService) {
        this.alerteService = alerteService;
    }

    @GetMapping("/alertese")
    public String listeAlertes(Model model) {
        List<Alerte> alertes = alerteService.toutesLesAlertes();
        model.addAttribute("alertes", alertes);
        return "alertes/alerte"; // correspond au fichier alertes.html Thymeleaf
    }
}
