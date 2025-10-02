package com.pagam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeteoController {

    @GetMapping("/meteo")
    public String showMeteoPage() {
        return "layout"; // correspond au fichier meteo.html dans templates
    }
}
