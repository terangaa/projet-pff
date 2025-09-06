package com.pagam.service;

import com.pagam.entity.Producteur;
import com.pagam.repository.ProducteurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProducteurService {

    private final ProducteurRepository producteurRepository;

    public ProducteurService(ProducteurRepository producteurRepository) {
        this.producteurRepository = producteurRepository;
    }

    public Producteur saveProducteur(Producteur producteur) {
        return producteurRepository.save(producteur);
    }

    public List<Producteur> getAllProducteurs() {
        return producteurRepository.findAll();
    }

    public Producteur getProducteurById(Long id) {
        return producteurRepository.findById(id).orElse(null);
    }

    public void deleteProducteur(Long id) {
        producteurRepository.deleteById(id);
    }
}
