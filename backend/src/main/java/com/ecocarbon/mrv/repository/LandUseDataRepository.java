package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.LandUseData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandUseDataRepository extends JpaRepository<LandUseData, Long> {
    List<LandUseData> findByAreaId(String areaId);

    List<LandUseData> findByAreaIdAndYear(String areaId, Integer year);

    List<LandUseData> findByLandType(String landType);

    List<LandUseData> findByAreaIdAndLandType(String areaId, String landType);
}
