package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.MrvReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MrvReportRepository extends JpaRepository<MrvReport, Long> {
    List<MrvReport> findByProjectId(Long projectId);

    List<MrvReport> findByReportType(String reportType);

    List<MrvReport> findByStatus(String status);
}
