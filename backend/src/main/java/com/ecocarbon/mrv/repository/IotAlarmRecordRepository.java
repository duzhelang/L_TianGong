package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.IotAlarmRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotAlarmRecordRepository extends JpaRepository<IotAlarmRecord, Long> {
    Page<IotAlarmRecord> findByStatus(String status, Pageable pageable);

    List<IotAlarmRecord> findByDeviceCode(String deviceCode);

    List<IotAlarmRecord> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatus(String status);
}
