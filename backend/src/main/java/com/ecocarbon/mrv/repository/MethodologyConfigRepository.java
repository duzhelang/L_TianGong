package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.MethodologyConfig;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MethodologyConfigRepository extends JpaRepository<MethodologyConfig, Long> {
    Optional<MethodologyConfig> findByMethodologyCode(String methodologyCode);
    
    List<MethodologyConfig> findByStatus(String status);
    
    List<MethodologyConfig> findByApplicableScenesContaining(String scene);
}