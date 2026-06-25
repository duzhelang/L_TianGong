package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.SensorData;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    Page<SensorData> findByDeviceCodeAndTimestampBetween(
            String deviceCode, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<SensorData> findByDeviceCodeAndMetricAndTimestampBetween(
            String deviceCode, String metric, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Optional<SensorData> findTopByDeviceCodeAndMetricOrderByTimestampDesc(String deviceCode, String metric);

    Page<SensorData> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT MIN(s.value), MAX(s.value), AVG(s.value), COUNT(s) FROM SensorData s " +
           "WHERE s.deviceCode = :deviceCode AND s.metric = :metric " +
           "AND s.timestamp BETWEEN :start AND :end")
    Object[] getStatistics(@Param("deviceCode") String deviceCode,
                          @Param("metric") String metric,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    long countByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
