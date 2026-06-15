package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.GreenFactorOptimizationRequest;
import com.ecocarbon.mrv.dto.GreenFactorOptimizationResponse;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.repository.EmissionFactorRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGreenFactorService {

    private final EmissionFactorRepository emissionFactorRepository;

    /**
     * AI推荐绿色电力因子
     *
     * @param request 优化请求
     * @return 优化响应，包含推荐因子和优化建议
     */
    public GreenFactorOptimizationResponse optimizeGreenFactor(GreenFactorOptimizationRequest request) {
        log.info("开始AI推荐绿色电力因子，项目ID: {}，地区: {}，电量: {}kWh",
                request.projectId(), request.region(), request.electricityKwh());

        List<EmissionFactor> greenFactors = findGreenFactors(request.region(), request.energyType());
        Double recommendedFactor = calculateRecommendedFactor(greenFactors, request);
        List<String> suggestions = generateOptimizationSuggestions(request, recommendedFactor);
        Double potentialReduction = calculatePotentialReduction(request.electricityKwh(), recommendedFactor);
        String confidenceLevel = determineConfidenceLevel(greenFactors.size(), request.region());

        return new GreenFactorOptimizationResponse(
                recommendedFactor,
                "kgCO2/kWh",
                "AI推荐",
                suggestions,
                potentialReduction,
                confidenceLevel
        );
    }

    /**
     * 查找绿色电力因子
     */
    private List<EmissionFactor> findGreenFactors(String region, String energyType) {
        List<EmissionFactor> factors = new ArrayList<>();
        if (energyType != null && !energyType.isEmpty()) {
            factors = emissionFactorRepository.findByCategoryAndStatus(energyType, "ACTIVE");
        }
        if (factors.isEmpty()) {
            factors = emissionFactorRepository.findByCategoryAndStatus("电力", "ACTIVE");
        }
        return factors.stream()
                .filter(f -> f.getRegion() == null || f.getRegion().contains(region))
                .toList();
    }

    /**
     * 计算推荐因子
     */
    private Double calculateRecommendedFactor(List<EmissionFactor> factors, GreenFactorOptimizationRequest request) {
        if (factors.isEmpty()) {
            return getDefaultGreenFactor(request.region());
        }
        return factors.stream()
                .mapToDouble(EmissionFactor::getFactorValue)
                .average()
                .orElse(getDefaultGreenFactor(request.region()));
    }

    /**
     * 获取默认绿色电力因子（基于地区）
     */
    private Double getDefaultGreenFactor(String region) {
        if (region.contains("北京") || region.contains("上海") || region.contains("广东")) {
            return 0.5;
        } else if (region.contains("新疆") || region.contains("青海") || region.contains("甘肃")) {
            return 0.3;
        }
        return 0.6;
    }

    /**
     * 生成优化建议
     */
    private List<String> generateOptimizationSuggestions(GreenFactorOptimizationRequest request, Double currentFactor) {
        List<String> suggestions = new ArrayList<>();

        if (currentFactor > 0.5) {
            suggestions.add("建议采购绿电证书（I-REC）以降低电力碳排放因子");
            suggestions.add("考虑安装分布式光伏系统，预计可降低30%-50%的电力碳排放");
        }
        if (request.electricityKwh() > 10000) {
            suggestions.add("大用户可申请直购绿电，享受更优惠的绿电价格");
            suggestions.add("建议签订长期绿电PPA协议，锁定绿色电力供应");
        }
        if (request.region().contains("新疆") || request.region().contains("青海")) {
            suggestions.add("该地区风光资源丰富，建议增加可再生能源自发电比例");
        }
        suggestions.add("定期更新电力排放因子，确保碳核算准确性");

        return suggestions;
    }

    /**
     * 计算潜在减排量（吨CO2）
     */
    private Double calculatePotentialReduction(Double electricityKwh, Double currentFactor) {
        double conventionalFactor = 0.7;
        double reduction = electricityKwh * (conventionalFactor - currentFactor) / 1000;
        return Math.max(0, Math.round(reduction * 100.0) / 100.0);
    }

    /**
     * 确定置信度等级
     */
    private String determineConfidenceLevel(int factorCount, String region) {
        if (factorCount >= 3) {
            return "高";
        } else if (factorCount >= 1) {
            return "中";
        }
        return "低";
    }
}