package com.pagam.repository;

import com.pagam.entity.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {

}
