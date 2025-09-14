package com.pagam.controller;

import com.pagam.entity.Produit;
import com.pagam.entity.Producteur;
import com.pagam.repository.ProduitRepository;
import com.pagam.service.ProduitService;
import com.pagam.service.ProducteurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitService produitService;
    private final ProducteurService producteurService;

    private final ProduitRepository produitRepository;

    public ProduitController(ProduitService produitService, ProducteurService producteurService, ProduitRepository produitRepository) {
        this.produitService = produitService;
        this.producteurService = producteurService;
        this.produitRepository = produitRepository;
    }

    // 📌 Liste de tous les produits
    @GetMapping
    public String listeProduits(Model model) {
        List<Produit> produits = produitService.getAllProduits();
        model.addAttribute("produits", produits);
        return "produits/produit"; // fichier produits/produit.html
    }

    // 📌 Afficher un produit par son ID
    @GetMapping("/produit/{id}")
    public String getProduit(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id);
        if (produit == null) {
            return "redirect:/produits"; // produit inexistant
        }

        if (produit.getProducteur() == null) {
            produit.setProducteur(new Producteur());
        }

        model.addAttribute("produit", produit);
        return "produits/produit-detail"; // ⚠️ à créer (différent de la liste)
    }

    // 📌 Formulaire de création
    @GetMapping("/creer")
    public String creerProduitForm(Model model) {
        model.addAttribute("produit", new Produit());
        List<Producteur> producteurs = producteurService.getAllProducteurs();
        model.addAttribute("producteurs", producteurs);
        return "produits/creer-produit";
    }

    // 📌 Enregistrer un nouveau produit
    @PostMapping("/creer")
    public String creerProduit(@ModelAttribute Produit produit,
                               @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("src/main/resources/static/images/produits/");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = imageFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            produit.setImage(fileName);
        }

        produitRepository.save(produit);
        return "redirect:/produits";
    }


    // 📌 Formulaire de modification
    @GetMapping("/modifier/{id}")
    public String modifierProduitForm(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id);
        if (produit == null) {
            return "redirect:/produits";
        }

        model.addAttribute("produit", produit);
        List<Producteur> producteurs = producteurService.getAllProducteurs();
        model.addAttribute("producteurs", producteurs);
        return "produits/modifier-produit";
    }

    // 📌 Enregistrer les modifications
    @PostMapping("/modifier/{id}")
    public String modifierProduit(@PathVariable Long id, @ModelAttribute Produit produit) {
        produitService.updateProduit(id, produit);
        return "redirect:/produits";
    }

    // 📌 Supprimer un produit (sécurisé)
    @GetMapping("/supprimer/{id}")
    public String supprimerProduit(@PathVariable Long id, Model model) {
        boolean supprimé = produitService.deleteProduit(id);

        if (!supprimé) {
            // Si suppression impossible → afficher message d’erreur
            model.addAttribute("error", "Impossible de supprimer ce produit : il est utilisé dans une commande !");
            return listeProduits(model);
        }

        return "redirect:/produits";
    }
}
