package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.ReportTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {
    Optional<ReportTemplate> findByTemplateCode(String templateCode);

    List<ReportTemplate> findByReportType(String reportType);

    List<ReportTemplate> findByStatus(String status);
    
    long countByReportType(String reportType);
    
    long countByStatus(String status);
}
