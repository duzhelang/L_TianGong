package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.CarbonProject;
import com.ecocarbon.mrv.entity.EnergyData;
import com.ecocarbon.mrv.repository.CarbonProjectRepository;
import com.ecocarbon.mrv.repository.EnergyDataRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnergyDataService {
    private final EnergyDataRepository energyDataRepository;
    private final CarbonProjectRepository projectRepository;

    public Page<EnergyData> list(Long projectId, String energyType,
                                  LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (energyType != null && !energyType.isEmpty()) {
            return energyDataRepository.findByProjectIdAndEnergyTypeAndRecordDateBetween(
                    projectId, energyType, startDate, endDate, pageable);
        }
        return energyDataRepository.findByProjectIdAndRecordDateBetween(
                projectId, startDate, endDate, pageable);
    }

    public EnergyData getById(Long id) {
        return energyDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("能耗数据不存在"));
    }

    @Transactional
    public EnergyData create(EnergyData data) {
        if (data.getProject() != null && data.getProject().getId() != null) {
            CarbonProject project = projectRepository.findById(data.getProject().getId())
                    .orElseThrow(() -> new IllegalArgumentException("项目不存在"));
            data.setProject(project);
        }
        data.setCreatedAt(LocalDateTime.now());
        data.setUpdatedAt(LocalDateTime.now());
        return energyDataRepository.save(data);
    }

    @Transactional
    public List<EnergyData> createBatch(List<EnergyData> dataList) {
        LocalDateTime now = LocalDateTime.now();
        for (EnergyData data : dataList) {
            if (data.getProject() != null && data.getProject().getId() != null) {
                CarbonProject project = projectRepository.findById(data.getProject().getId())
                        .orElseThrow(() -> new IllegalArgumentException("项目不存在: " + data.getProject().getId()));
                data.setProject(project);
            }
            data.setCreatedAt(now);
            data.setUpdatedAt(now);
        }
        return energyDataRepository.saveAll(dataList);
    }

    @Transactional
    public EnergyData update(Long id, EnergyData data) {
        EnergyData existing = getById(id);
        existing.setEnergyType(data.getEnergyType());
        existing.setEnergyCategory(data.getEnergyCategory());
        existing.setConsumption(data.getConsumption());
        existing.setUnit(data.getUnit());
        existing.setEmissionFactor(data.getEmissionFactor());
        existing.setEmissionUnit(data.getEmissionUnit());
        existing.setCarbonEmission(data.getCarbonEmission());
        existing.setRecordDate(data.getRecordDate());
        existing.setSource(data.getSource());
        existing.setQuality(data.getQuality());
        existing.setRemark(data.getRemark());
        existing.setUpdatedAt(LocalDateTime.now());
        return energyDataRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        energyDataRepository.deleteById(id);
    }

    public Map<String, Object> getStatistics(Long projectId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();
        Double totalEmission = energyDataRepository.getTotalEmission(projectId, startDate, endDate);
        stats.put("totalEmission", totalEmission != null ? totalEmission : 0.0);
        stats.put("projectId", projectId);
        stats.put("startDate", startDate);
        stats.put("endDate", endDate);

        List<Object[]> byType = energyDataRepository.getEmissionByType(projectId, startDate, endDate);
        List<Map<String, Object>> typeBreakdown = new ArrayList<>();
        for (Object[] row : byType) {
            Map<String, Object> item = new HashMap<>();
            item.put("energyType", row[0]);
            item.put("totalConsumption", row[1]);
            item.put("totalEmission", row[2]);
            typeBreakdown.add(item);
        }
        stats.put("typeBreakdown", typeBreakdown);
        return stats;
    }

    @Transactional
    public EnergyData calculateAndSave(Long projectId, String energyType, String energyCategory,
                                        Double consumption, String unit, LocalDate recordDate, String source) {
        double emissionFactor = getEmissionFactor(energyType);
        String emissionUnit = "kgCO2/" + getEmissionFactorUnit(energyType);
        double carbonEmission = consumption * emissionFactor;

        EnergyData data = new EnergyData();
        CarbonProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("项目不存在"));
        data.setProject(project);
        data.setEnergyType(energyType);
        data.setEnergyCategory(energyCategory);
        data.setConsumption(consumption);
        data.setUnit(unit);
        data.setEmissionFactor(emissionFactor);
        data.setEmissionUnit(emissionUnit);
        data.setCarbonEmission(carbonEmission);
        data.setRecordDate(recordDate);
        data.setSource(source);
        data.setCreatedAt(LocalDateTime.now());
        data.setUpdatedAt(LocalDateTime.now());
        return energyDataRepository.save(data);
    }

    private double getEmissionFactor(String energyType) {
        return switch (energyType) {
            case "ELECTRICITY" -> 0.5810;
            case "DIESEL" -> 2.68;
            case "GASOLINE" -> 2.30;
            case "NATURAL_GAS" -> 2.16;
            case "COAL" -> 2.42;
            case "LPG" -> 2.98;
            case "HEATING" -> 0.30;
            default -> 1.0;
        };
    }

    private String getEmissionFactorUnit(String energyType) {
        return switch (energyType) {
            case "ELECTRICITY" -> "kWh";
            case "DIESEL", "GASOLINE" -> "L";
            case "NATURAL_GAS", "LPG" -> "m3";
            case "COAL" -> "kg";
            case "HEATING" -> "MJ";
            default -> "unit";
        };
    }
}
