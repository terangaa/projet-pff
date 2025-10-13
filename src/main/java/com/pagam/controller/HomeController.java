package com.pagam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "homes/home";
    }

    @GetMapping("/services")
    public String services() {
        return "homes/service";
    }

    @GetMapping("/contact")
    public String contact() {
        return "homes/contact";
    }

    @GetMapping("/about")
    public String about() {
        return "homes/about";
    }
}
