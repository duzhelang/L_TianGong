package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.ReportTemplate;
import com.ecocarbon.mrv.repository.ReportTemplateRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportTemplateService {
    private final ReportTemplateRepository reportTemplateRepository;

    public Page<ReportTemplate> list(Pageable pageable) {
        return reportTemplateRepository.findAll(pageable);
    }

    public List<ReportTemplate> getByReportType(String reportType) {
        return reportTemplateRepository.findByReportType(reportType);
    }

    public List<ReportTemplate> getActiveTemplates() {
        return reportTemplateRepository.findByStatus("ACTIVE");
    }

    public ReportTemplate getById(Long id) {
        return reportTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("报告模板不存在: " + id));
    }

    public ReportTemplate getByCode(String templateCode) {
        return reportTemplateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("报告模板不存在: " + templateCode));
    }

    @Transactional
    public ReportTemplate create(ReportTemplate template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        if (template.getStatus() == null) {
            template.setStatus("ACTIVE");
        }
        return reportTemplateRepository.save(template);
    }

    @Transactional
    public ReportTemplate update(Long id, ReportTemplate template) {
        ReportTemplate existing = getById(id);
        if (template.getTemplateName() != null) {
            existing.setTemplateName(template.getTemplateName());
        }
        if (template.getReportType() != null) {
            existing.setReportType(template.getReportType());
        }
        if (template.getTemplatePath() != null) {
            existing.setTemplatePath(template.getTemplatePath());
        }
        if (template.getDescription() != null) {
            existing.setDescription(template.getDescription());
        }
        if (template.getStatus() != null) {
            existing.setStatus(template.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return reportTemplateRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ReportTemplate template = getById(id);
        reportTemplateRepository.delete(template);
    }

    public long countByType(String reportType) {
        return reportTemplateRepository.countByReportType(reportType);
    }

    public long countActive() {
        return reportTemplateRepository.countByStatus("ACTIVE");
    }
}
