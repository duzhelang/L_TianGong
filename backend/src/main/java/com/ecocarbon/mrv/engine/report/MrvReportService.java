package com.ecocarbon.mrv.engine.report;

import com.ecocarbon.mrv.entity.MrvReport;
import com.ecocarbon.mrv.repository.MrvReportRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MrvReportService {
    private final MrvReportRepository reportRepository;

    public Page<MrvReport> list(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    public List<MrvReport> getByProjectId(Long projectId) {
        return reportRepository.findByProjectId(projectId);
    }

    public MrvReport getById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报告不存在"));
    }

    public MrvReport create(MrvReport report) {
        report.setStatus("DRAFT");
        report.setVersion(1);
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public MrvReport updateStatus(Long id, String status) {
        MrvReport report = getById(id);
        report.setStatus(status);
        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public void delete(Long id) {
        reportRepository.deleteById(id);
    }
}
