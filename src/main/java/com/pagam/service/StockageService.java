package com.pagam.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class StockageService {

    private final Path basePath = Paths.get("src/main/resources/static/images");

    /**
     * Sauvegarde une image dans un sous-dossier (produits, utilisateurs...) et retourne son chemin relatif
     */
    public String save(MultipartFile imageFile, String subFolder) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        try {
            // Crée le dossier si besoin
            Path uploadPath = basePath.resolve(subFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Nettoie le nom original
            String originalFilename = imageFile.getOriginalFilename();
            if (originalFilename == null) return null;

            String cleanName = originalFilename
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9._-]", "");

            // Nom unique
            String filename = System.currentTimeMillis() + "_" + cleanName;

            // Copie dans le répertoire
            Path filePath = uploadPath.resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ⚡ Retourne un chemin relatif pour le frontend
            return "/images/" + subFolder + "/" + filename;  // <-- ajouter "images"
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la sauvegarde de l’image : " + e.getMessage());
            return null;
        }
    }
    /**
     * Supprime une image existante
     */
    public void delete(String imagePath, String subFolder) {
        if (imagePath == null || imagePath.isEmpty()) return;

        try {
            // Exemple : "/utilisateurs/12345_photo.jpg" -> "12345_photo.jpg"
            String relative = Paths.get(imagePath).getFileName().toString();

            Path fullPath = basePath.resolve(subFolder).resolve(relative);
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);
                System.out.println("✅ Image supprimée : " + fullPath);
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la suppression de l’image : " + e.getMessage());
        }
    }
}
