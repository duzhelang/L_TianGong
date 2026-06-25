package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.ActivityData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityDataRepository extends JpaRepository<ActivityData, Long> {
    List<ActivityData> findByProjectId(Long projectId);
    
    List<ActivityData> findByDataType(String dataType);
    
    List<ActivityData> findByActivityCategory(String activityCategory);
    
    List<ActivityData> findByProjectIdAndDataType(Long projectId, String dataType);
    
    long countByProjectId(Long projectId);
    
    long countByDataType(String dataType);
}