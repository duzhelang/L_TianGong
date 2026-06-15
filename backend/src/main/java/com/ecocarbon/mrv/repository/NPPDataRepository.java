package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.NPPData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NPPDataRepository extends JpaRepository<NPPData, Long> {
    List<NPPData> findByAreaId(String areaId);

    List<NPPData> findByAreaIdAndYear(String areaId, Integer year);

    List<NPPData> findByAreaIdAndYearAndMonth(String areaId, Integer year, Integer month);

    List<NPPData> findByAreaIdAndYearBetween(String areaId, Integer startYear, Integer endYear);
}
