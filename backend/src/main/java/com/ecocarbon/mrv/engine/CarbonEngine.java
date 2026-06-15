package com.ecocarbon.mrv.engine;

import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.CarbonEmission;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.entity.MethodologyConfig;
import com.ecocarbon.mrv.service.CarbonCalculationService;
import com.ecocarbon.mrv.service.EmissionFactorService;
import com.ecocarbon.mrv.service.MethodologyService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarbonEngine {
    private final CarbonCalculationService carbonCalculationService;
    private final EmissionFactorService emissionFactorService;
    private final MethodologyService methodologyService;
    private final ScopeCalculator scopeCalculator;

    public CarbonEmission calculate(ActivityData activityData, EmissionFactor emissionFactor, String scope, String methodologyCode) {
        MethodologyConfig methodology = methodologyService.findByMethodologyCode(methodologyCode)
                .orElseThrow(() -> new RuntimeException("方法学配置不存在"));

        double emissionValue = scopeCalculator.calculateByScope(activityData, emissionFactor, scope, methodology);

        CarbonEmission emission = new CarbonEmission();
        emission.setProject(activityData.getProject());
        emission.setActivityData(activityData);
        emission.setEmissionFactor(emissionFactor);
        emission.setScope(scope);
        emission.setGasType(emissionFactor.getGasType());
        emission.setEmissionValue(emissionValue);
        emission.setUnit(emissionFactor.getUnit());
        emission.setCalculationMethod(methodology.getMethodologyCode());
        emission.setPeriodStart(activityData.getPeriodStart());
        emission.setPeriodEnd(activityData.getPeriodEnd());
        emission.setStatus("ACTIVE");

        return emission;
    }

    public Map<String, Double> calculateProjectEmissions(Long projectId) {
        Map<String, Double> results = new HashMap<>();

        Double scope1 = carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_1");
        Double scope2 = carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_2");
        Double scope3 = carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_3");
        Double total = carbonCalculationService.getTotalEmission(projectId);

        results.put("scope1", scope1 != null ? scope1 : 0.0);
        results.put("scope2", scope2 != null ? scope2 : 0.0);
        results.put("scope3", scope3 != null ? scope3 : 0.0);
        results.put("total", total != null ? total : 0.0);

        return results;
    }

    public List<CarbonEmission> batchCalculate(Long projectId, String scope) {
        return carbonCalculationService.batchCalculate(projectId, scope);
    }

    public CarbonEmission getEmissionById(Long id) {
        return carbonCalculationService.getEmissionsByProject(id).stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}