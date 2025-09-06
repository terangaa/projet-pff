package com.pagam.repository;

import com.pagam.entity.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    List<Alerte> findByTypeOrderByTimestampDesc(String type);
}
