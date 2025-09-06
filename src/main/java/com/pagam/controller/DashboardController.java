package com.pagam.controller;

import com.pagam.entity.Alerte;
import com.pagam.entity.Capteur;
import com.pagam.service.AlerteService;
import com.pagam.service.CapteurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CapteurService capteurService;
    private final AlerteService alerteService;

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Capteur> capteurs = capteurService.tousLesCapteurs();
        capteurs.forEach(c -> c.setMesures(capteurService.getDernieresMesures(c.getId())));
        model.addAttribute("capteurs", capteurs);
        model.addAttribute("alertSeuil", (AlertSeuilFunction) capteurService::estAlerte);
        model.addAttribute("alertes", alerteService.getToutes());
        return "dashboard/dashboard";
    }

    @GetMapping("/alertes")
    public String alertes(@RequestParam(required = false) String type, Model model) {
        List<Alerte> alertes = (type == null || type.isEmpty()) ?
                alerteService.getToutes() :
                alerteService.getParType(type);
        model.addAttribute("alertes", alertes);
        model.addAttribute("selectedType", type);
        return "alertes/alerte";
    }

    @FunctionalInterface
    interface AlertSeuilFunction {
        boolean apply(double valeur, String type);
    }
}

