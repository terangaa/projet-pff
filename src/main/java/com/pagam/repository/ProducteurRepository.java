package com.pagam.repository;

import com.pagam.entity.Producteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducteurRepository extends JpaRepository<Producteur, Long> {
}

