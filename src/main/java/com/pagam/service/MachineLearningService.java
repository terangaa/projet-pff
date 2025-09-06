package com.pagam.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

@Service
public class MachineLearningService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String mlUrl = "http://localhost:5000/predict";

    public int getPrediction(double temperature, double humidite) {
        Map<String, Object> body = new HashMap<>();
        body.put("temperature", temperature);
        body.put("humidite", humidite);

        Map<String, Integer> response = restTemplate.postForObject(mlUrl, body, Map.class);
        return response.get("prediction");
    }
}
