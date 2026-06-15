package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.CarbonEmission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonEmissionRepository extends JpaRepository<CarbonEmission, Long> {
    List<CarbonEmission> findByProjectId(Long projectId);
    
    List<CarbonEmission> findByScope(String scope);
    
    List<CarbonEmission> findByGasType(String gasType);
    
    List<CarbonEmission> findByProjectIdAndScope(Long projectId, String scope);
    
    List<CarbonEmission> findByProjectIdAndGasType(Long projectId, String gasType);
}