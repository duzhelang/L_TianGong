package com.ecocarbon.mrv.engine;

import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.CarbonEmission;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.entity.MethodologyConfig;
import com.ecocarbon.mrv.service.CarbonCalculationService;
import com.ecocarbon.mrv.service.EmissionFactorService;
import com.ecocarbon.mrv.service.MethodologyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("碳核算引擎测试")
class CarbonEngineTest {

    @Mock
    private CarbonCalculationService carbonCalculationService;

    @Mock
    private EmissionFactorService emissionFactorService;

    @Mock
    private MethodologyService methodologyService;

    @Mock
    private ScopeCalculator scopeCalculator;

    @InjectMocks
    private CarbonEngine carbonEngine;

    private ActivityData activityData;
    private EmissionFactor emissionFactor;
    private MethodologyConfig methodologyConfig;

    @BeforeEach
    void setUp() {
        activityData = new ActivityData();
        activityData.setId(1L);
        activityData.setValue(1000.0);
        activityData.setUnit("L");
        activityData.setPeriodStart(LocalDate.of(2024, 1, 1));
        activityData.setPeriodEnd(LocalDate.of(2024, 12, 31));

        emissionFactor = new EmissionFactor();
        emissionFactor.setId(1L);
        emissionFactor.setFactorCode("EF-CO2-001");
        emissionFactor.setGasType("CO2");
        emissionFactor.setFactorValue(2.68);
        emissionFactor.setUnit("kgCO2/L");

        methodologyConfig = new MethodologyConfig();
        methodologyConfig.setId(1L);
        methodologyConfig.setMethodologyCode("IPCC-2006");
        methodologyConfig.setMethodologyName("IPCC 2006指南");
    }

    @Test
    @DisplayName("计算单个活动数据排放量 - 成功")
    void calculate_Success() {
        when(methodologyService.findByMethodologyCode("IPCC-2006"))
                .thenReturn(Optional.of(methodologyConfig));
        when(scopeCalculator.calculateByScope(any(), any(), eq("SCOPE_1"), any()))
                .thenReturn(2680.0);

        CarbonEmission result = carbonEngine.calculate(activityData, emissionFactor, "SCOPE_1", "IPCC-2006");

        assertNotNull(result);
        assertEquals(2680.0, result.getEmissionValue());
        assertEquals("CO2", result.getGasType());
        assertEquals("SCOPE_1", result.getScope());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    @DisplayName("计算单个活动数据排放量 - 方法学不存在")
    void calculate_MethodologyNotFound() {
        when(methodologyService.findByMethodologyCode("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            carbonEngine.calculate(activityData, emissionFactor, "SCOPE_1", "INVALID");
        });
    }

    @Test
    @DisplayName("计算项目总排放量 - 成功")
    void calculateProjectEmissions_Success() {
        Long projectId = 1L;
        when(carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_1"))
                .thenReturn(1000.0);
        when(carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_2"))
                .thenReturn(500.0);
        when(carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_3"))
                .thenReturn(200.0);
        when(carbonCalculationService.getTotalEmission(projectId))
                .thenReturn(1700.0);

        Map<String, Double> result = carbonEngine.calculateProjectEmissions(projectId);

        assertEquals(1000.0, result.get("scope1"));
        assertEquals(500.0, result.get("scope2"));
        assertEquals(200.0, result.get("scope3"));
        assertEquals(1700.0, result.get("total"));
    }

    @Test
    @DisplayName("计算项目总排放量 - 部分范围为空")
    void calculateProjectEmissions_PartialNull() {
        Long projectId = 1L;
        when(carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_1"))
                .thenReturn(1000.0);
        when(carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_2"))
                .thenReturn(null);
        when(carbonCalculationService.getTotalEmissionByScope(projectId, "SCOPE_3"))
                .thenReturn(null);
        when(carbonCalculationService.getTotalEmission(projectId))
                .thenReturn(1000.0);

        Map<String, Double> result = carbonEngine.calculateProjectEmissions(projectId);

        assertEquals(1000.0, result.get("scope1"));
        assertEquals(0.0, result.get("scope2"));
        assertEquals(0.0, result.get("scope3"));
        assertEquals(1000.0, result.get("total"));
    }

    @Test
    @DisplayName("批量计算排放量 - 成功")
    void batchCalculate_Success() {
        Long projectId = 1L;
        List<CarbonEmission> emissions = Arrays.asList(
                createEmission(1L, 1000.0),
                createEmission(2L, 500.0)
        );
        when(carbonCalculationService.batchCalculate(projectId, "SCOPE_1"))
                .thenReturn(emissions);

        List<CarbonEmission> result = carbonEngine.batchCalculate(projectId, "SCOPE_1");

        assertEquals(2, result.size());
        assertEquals(1000.0, result.get(0).getEmissionValue());
        assertEquals(500.0, result.get(1).getEmissionValue());
    }

    private CarbonEmission createEmission(Long id, double value) {
        CarbonEmission emission = new CarbonEmission();
        emission.setId(id);
        emission.setEmissionValue(value);
        return emission;
    }
}
