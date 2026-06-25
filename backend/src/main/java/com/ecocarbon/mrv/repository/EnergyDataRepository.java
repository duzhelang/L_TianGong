package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.EnergyData;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {

    Page<EnergyData> findByProjectIdAndRecordDateBetween(
            Long projectId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<EnergyData> findByProjectIdAndEnergyTypeAndRecordDateBetween(
            Long projectId, String energyType, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<EnergyData> findByProjectIdAndRecordDateBetween(
            Long projectId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.consumption) FROM EnergyData e WHERE e.project.id = :projectId " +
           "AND e.energyType = :energyType AND e.recordDate BETWEEN :start AND :end")
    Double getTotalConsumption(@Param("projectId") Long projectId,
                               @Param("energyType") String energyType,
                               @Param("start") LocalDate start,
                               @Param("end") LocalDate end);

    @Query("SELECT SUM(e.carbonEmission) FROM EnergyData e WHERE e.project.id = :projectId " +
           "AND e.recordDate BETWEEN :start AND :end")
    Double getTotalEmission(@Param("projectId") Long projectId,
                            @Param("start") LocalDate start,
                            @Param("end") LocalDate end);

    @Query("SELECT e.energyType, SUM(e.consumption), SUM(e.carbonEmission) FROM EnergyData e " +
           "WHERE e.project.id = :projectId AND e.recordDate BETWEEN :start AND :end " +
           "GROUP BY e.energyType")
    List<Object[]> getEmissionByType(@Param("projectId") Long projectId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);
}
