package com.pagam.controller;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class SimulationController {

    private final Random random = new Random();

    @GetMapping("simulations")
    public String simulation(Model model) {

        List<Capteur> capteurs = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Capteur c = Capteur.builder()
                    .id((long) i)
                    .reference("CAP-00" + i)
                    .type(i % 2 == 0 ? "HUMIDITE" : "TEMP")
                    .localisation("Région " + i)
                    .mesures(generateMesuresSimulees())
                    .build();
            capteurs.add(c);
        }

        model.addAttribute("capteurs", capteurs);
        return "simulation/list-simulation";
    }

    private List<Mesure> generateMesuresSimulees() {
        List<Mesure> mesures = new ArrayList<>();
        mesures.add(Mesure.builder()
                .temperature(15 + random.nextDouble() * 20)
                .humidite(30 + random.nextDouble() * 50)
                .luminosite(random.nextDouble() * 1000)
                .besoinEau(random.nextInt(5))
                .horodatage(java.time.LocalDateTime.now().toString())
                .build());
        return mesures;
    }
}

