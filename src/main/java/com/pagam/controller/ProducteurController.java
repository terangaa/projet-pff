package com.pagam.controller;

import com.pagam.entity.Producteur;
import com.pagam.repository.ProducteurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/producteurs")
public class ProducteurController {

    private final ProducteurRepository producteurRepository;

    public ProducteurController(ProducteurRepository producteurRepository) {
        this.producteurRepository = producteurRepository;
    }

    // Liste HTML
    @GetMapping
    public String getTousHtml(Model model) {
        model.addAttribute("producteurs", producteurRepository.findAll());
        return "producteurs/producteur"; // templates/producteurs/producteurs.html
    }

    // Créer producteur - Formulaire
    @GetMapping("/creer")
    public String creerForm(Model model) {
        model.addAttribute("producteur", new Producteur());
        return "producteurs/creer-producteur"; // templates/producteurs/creer-producteur.html
    }

    // Créer producteur - Soumission
    @PostMapping("/creer")
    public String creerHtml(@ModelAttribute Producteur producteur) {
        producteurRepository.save(producteur);
        return "redirect:/producteurs";
    }

    // Modifier producteur - Formulaire
    @GetMapping("/modifier/{id}")
    public String modifierForm(@PathVariable Long id, Model model) {
        Producteur producteur = producteurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producteur non trouvé : " + id));
        model.addAttribute("producteur", producteur);
        return "producteurs/modifier-producteur"; // templates/producteurs/modifier-producteur.html
    }

    // Modifier producteur - Soumission
    @PostMapping("/modifier/{id}")
    public String modifierHtml(@PathVariable Long id, @ModelAttribute Producteur producteur) {
        producteur.setId(id);
        producteurRepository.save(producteur);
        return "redirect:/producteurs";
    }

    // Supprimer producteur
    @GetMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id) {
        producteurRepository.deleteById(id);
        return "redirect:/producteurs";
    }
}
