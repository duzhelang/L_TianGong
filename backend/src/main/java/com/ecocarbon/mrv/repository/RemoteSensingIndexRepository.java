package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.RemoteSensingIndex;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemoteSensingIndexRepository extends JpaRepository<RemoteSensingIndex, Long> {
    List<RemoteSensingIndex> findByAreaId(String areaId);

    List<RemoteSensingIndex> findByIndexType(String indexType);

    List<RemoteSensingIndex> findByAreaIdAndIndexType(String areaId, String indexType);

    List<RemoteSensingIndex> findByAreaIdAndIndexTypeAndCaptureDateBetween(
            String areaId, String indexType, LocalDate startDate, LocalDate endDate);
}
