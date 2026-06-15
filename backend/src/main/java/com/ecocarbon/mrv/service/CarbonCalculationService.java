package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.CarbonEmission;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.repository.ActivityDataRepository;
import com.ecocarbon.mrv.repository.CarbonEmissionRepository;
import com.ecocarbon.mrv.repository.EmissionFactorRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarbonCalculationService {
    private final CarbonEmissionRepository carbonEmissionRepository;
    private final ActivityDataRepository activityDataRepository;
    private final EmissionFactorRepository emissionFactorRepository;

    public CarbonEmission calculateEmission(Long projectId, Long activityDataId, Long emissionFactorId, String scope) {
        ActivityData activityData = activityDataRepository.findById(activityDataId)
                .orElseThrow(() -> new RuntimeException("活动数据不存在"));
        EmissionFactor emissionFactor = emissionFactorRepository.findById(emissionFactorId)
                .orElseThrow(() -> new RuntimeException("排放因子不存在"));

        double emissionValue = activityData.getValue() * emissionFactor.getFactorValue();

        CarbonEmission emission = new CarbonEmission();
        emission.setProject(activityData.getProject());
        emission.setActivityData(activityData);
        emission.setEmissionFactor(emissionFactor);
        emission.setScope(scope);
        emission.setGasType(emissionFactor.getGasType());
        emission.setEmissionValue(emissionValue);
        emission.setUnit(emissionFactor.getUnit());
        emission.setCalculationMethod("ACTIVITY_FACTOR_METHOD");
        emission.setPeriodStart(activityData.getPeriodStart());
        emission.setPeriodEnd(activityData.getPeriodEnd());
        emission.setStatus("ACTIVE");
        emission.setCreatedAt(LocalDateTime.now());
        emission.setUpdatedAt(LocalDateTime.now());

        return carbonEmissionRepository.save(emission);
    }

    public List<CarbonEmission> batchCalculate(Long projectId, String scope) {
        List<ActivityData> activityDataList = activityDataRepository.findByProjectId(projectId);
        List<CarbonEmission> results = new ArrayList<>();

        for (ActivityData activityData : activityDataList) {
            EmissionFactor factor = findMatchingFactor(activityData);
            if (factor != null) {
                CarbonEmission emission = calculateEmission(projectId, activityData.getId(), factor.getId(), scope);
                results.add(emission);
            }
        }

        return results;
    }

    private EmissionFactor findMatchingFactor(ActivityData activityData) {
        List<EmissionFactor> factors = emissionFactorRepository.findByCategory(activityData.getActivityCategory());
        return factors.stream()
                .filter(f -> f.getStatus().equals("ACTIVE"))
                .findFirst()
                .orElse(null);
    }

    public List<CarbonEmission> getEmissionsByProject(Long projectId) {
        return carbonEmissionRepository.findByProjectId(projectId);
    }

    public List<CarbonEmission> getEmissionsByScope(Long projectId, String scope) {
        return carbonEmissionRepository.findByProjectIdAndScope(projectId, scope);
    }

    public Double getTotalEmission(Long projectId) {
        List<CarbonEmission> emissions = carbonEmissionRepository.findByProjectId(projectId);
        return emissions.stream()
                .mapToDouble(CarbonEmission::getEmissionValue)
                .sum();
    }

    public Double getTotalEmissionByScope(Long projectId, String scope) {
        List<CarbonEmission> emissions = carbonEmissionRepository.findByProjectIdAndScope(projectId, scope);
        return emissions.stream()
                .mapToDouble(CarbonEmission::getEmissionValue)
                .sum();
    }
}