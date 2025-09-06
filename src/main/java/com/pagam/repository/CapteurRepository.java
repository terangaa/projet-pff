package com.pagam.repository;

import com.pagam.entity.Capteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapteurRepository extends JpaRepository<Capteur, Long> {
}
