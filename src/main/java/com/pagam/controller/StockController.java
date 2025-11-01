package com.pagam.controller;

import com.pagam.entity.Produit;
import com.pagam.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private ProduitRepository produitRepository;

    // ✅ Ajouter du stock à un produit
    @PostMapping("/ajouter/{id}")
    public String ajouterStock(@PathVariable("id") Long id,
                               @RequestParam("quantite") int quantiteAjoutee) {

        Produit produit = produitRepository.findById(id).orElse(null);
        if (produit != null) {
            int ancienStock = produit.getStock();
            produit.setStock(ancienStock + quantiteAjoutee);
            produitRepository.save(produit);
        }

        return "redirect:/produits"; // Redirige vers la liste après modification
    }

    // ✅ Diminuer du stock (optionnel)
    @PostMapping("/retirer/{id}")
    public String retirerStock(@PathVariable("id") Long id,
                               @RequestParam("quantite") int quantiteRetiree) {

        Produit produit = produitRepository.findById(id).orElse(null);
        if (produit != null) {
            int ancienStock = produit.getStock();
            int nouveauStock = Math.max(0, ancienStock - quantiteRetiree);
            produit.setStock(nouveauStock);
            produitRepository.save(produit);
        }

        return "redirect:/produits";
    }

    // 🔁 Mise à jour du stock via fetch() depuis ton JS
    @PostMapping("/modifier-stock/{id}")
    @ResponseBody
    public String modifierStock(@PathVariable("id") Long id, @RequestParam("stock") int nouveauStock) {
        Produit produit = produitRepository.findById(id).orElse(null);
        if (produit != null) {
            produit.setStock(nouveauStock);
            produitRepository.save(produit);  // ✅ Persistance directe en base
            return "OK";
        }
        return "ERREUR";
    }

}
