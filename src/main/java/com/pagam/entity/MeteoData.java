package com.pagam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeteoData {
    private double temperature;
    private double humidite;
}

