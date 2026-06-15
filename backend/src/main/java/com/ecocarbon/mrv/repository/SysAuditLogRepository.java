package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.SysAuditLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysAuditLogRepository extends JpaRepository<SysAuditLog, Long> {
    Page<SysAuditLog> findByUserId(Long userId, Pageable pageable);

    List<SysAuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Page<SysAuditLog> findByOperation(String operation, Pageable pageable);
}
