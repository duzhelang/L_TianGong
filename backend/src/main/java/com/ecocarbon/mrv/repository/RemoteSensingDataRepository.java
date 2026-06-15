package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.RemoteSensingData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemoteSensingDataRepository extends JpaRepository<RemoteSensingData, Long> {
    List<RemoteSensingData> findByProjectId(Long projectId);
    
    List<RemoteSensingData> findByDataType(String dataType);
    
    List<RemoteSensingData> findBySatelliteSource(String satelliteSource);
    
    List<RemoteSensingData> findByProjectIdAndDataType(Long projectId, String dataType);
}