package com.ecocarbon.mrv.engine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MRVReportGenerator {

    public Map<String, Object> generateCCERReport(String projectName, String projectCode,
                                                   LocalDate reportingPeriodStart,
                                                   LocalDate reportingPeriodEnd,
                                                   Map<String, Double> scopeEmissions,
                                                   Map<String, Double> carbonSink,
                                                   Map<String, Object> uncertaintyAnalysis) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "CCER");
        report.put("reportVersion", "1.0");
        report.put("generationTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        report.put("projectName", projectName);
        report.put("projectCode", projectCode);
        report.put("reportingPeriod", Map.of(
                "start", reportingPeriodStart.toString(),
                "end", reportingPeriodEnd.toString()
        ));

        double totalEmission = scopeEmissions.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalSink = carbonSink.values().stream().mapToDouble(Double::doubleValue).sum();
        double netEmission = totalEmission - totalSink;

        report.put("emissionSummary", Map.of(
                "scope1", round(scopeEmissions.getOrDefault("scope1", 0.0)),
                "scope2", round(scopeEmissions.getOrDefault("scope2", 0.0)),
                "scope3", round(scopeEmissions.getOrDefault("scope3", 0.0)),
                "totalEmission", round(totalEmission),
                "unit", "tCO2e"
        ));

        report.put("carbonSinkSummary", Map.of(
                "soilCarbonSequestration", round(carbonSink.getOrDefault("soil", 0.0)),
                "vegetationCarbonSequestration", round(carbonSink.getOrDefault("vegetation", 0.0)),
                "totalSink", round(totalSink),
                "unit", "tCO2e"
        ));

        report.put("netEmission", Map.of(
                "value", round(netEmission),
                "unit", "tCO2e"
        ));

        if (uncertaintyAnalysis != null) {
            report.put("uncertaintyAnalysis", uncertaintyAnalysis);
        }

        report.put("verificationStatus", "PENDING");
        report.put("sections", generateCCERSections());
        return report;
    }

    public Map<String, Object> generateISO14064Report(String organizationName,
                                                       String organizationCode,
                                                       int reportingYear,
                                                       Map<String, Double> scopeEmissions,
                                                       Map<String, Double> emissionReductions,
                                                       Map<String, Object> boundaryInfo) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("reportType", "ISO_14064");
        report.put("reportVersion", "2018");
        report.put("generationTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        report.put("organizationName", organizationName);
        report.put("organizationCode", organizationCode);
        report.put("reportingYear", reportingYear);

        report.put("organizationalBoundary", boundaryInfo);

        double totalEmission = scopeEmissions.values().stream().mapToDouble(Double::doubleValue).sum();
        report.put("ghgEmissions", Map.of(
                "scope1", round(scopeEmissions.getOrDefault("scope1", 0.0)),
                "scope2", round(scopeEmissions.getOrDefault("scope2", 0.0)),
                "scope3", round(scopeEmissions.getOrDefault("scope3", 0.0)),
                "biogenicEmissions", round(scopeEmissions.getOrDefault("biogenic", 0.0)),
                "totalGHGEmissions", round(totalEmission),
                "unit", "tCO2e"
        ));

        if (emissionReductions != null && !emissionReductions.isEmpty()) {
            double totalReduction = emissionReductions.values().stream().mapToDouble(Double::doubleValue).sum();
            report.put("ghgRemovals", Map.of(
                    "removals", round(emissionReductions.getOrDefault("removals", 0.0)),
                    "offsets", round(emissionReductions.getOrDefault("offsets", 0.0)),
                    "totalRemovals", round(totalReduction),
                    "unit", "tCO2e"
            ));
        }

        report.put("sections", generateISO14064Sections());
        return report;
    }

    public Map<String, Object> generateEmissionInventory(Map<String, List<Map<String, Object>>> emissionDetails) {
        Map<String, Object> inventory = new LinkedHashMap<>();
        inventory.put("inventoryType", "GHG_EMISSION_INVENTORY");
        inventory.put("generationTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        double totalEmission = 0;
        Map<String, Double> scopeTotals = new LinkedHashMap<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : emissionDetails.entrySet()) {
            String scope = entry.getKey();
            double scopeTotal = 0;
            for (Map<String, Object> detail : entry.getValue()) {
                scopeTotal += (Double) detail.getOrDefault("emission", 0.0);
            }
            scopeTotals.put(scope, round(scopeTotal));
            totalEmission += scopeTotal;
        }

        inventory.put("scopeTotals", scopeTotals);
        inventory.put("totalEmission", round(totalEmission));
        inventory.put("unit", "tCO2e");
        inventory.put("emissionDetails", emissionDetails);
        return inventory;
    }

    public Map<String, Object> generatePDFReportTemplate(Map<String, Object> reportData) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("format", "PDF");
        template.put("title", reportData.getOrDefault("projectName", "MRV Report"));
        template.put("subtitle", reportData.getOrDefault("reportType", ""));

        List<Map<String, Object>> sections = new ArrayList<>();

        sections.add(createSection("封面", "cover", reportData));
        sections.add(createSection("目录", "tableOfContents", null));
        sections.add(createSection("项目概述", "projectOverview", reportData));
        sections.add(createSection("核算边界", "accountingBoundary", reportData));
        sections.add(createSection("排放源识别", "emissionSources", reportData));
        sections.add(createSection("活动数据", "activityData", reportData));
        sections.add(createSection("排放因子", "emissionFactors", reportData));
        sections.add(createSection("排放计算", "emissionCalculation", reportData));
        sections.add(createSection("不确定性分析", "uncertaintyAnalysis", reportData));
        sections.add(createSection("减排措施", "emissionReduction", reportData));
        sections.add(createSection("结论与建议", "conclusion", reportData));
        sections.add(createSection("附录", "appendix", reportData));

        template.put("sections", sections);
        template.put("totalPages", sections.size() + 2);
        return template;
    }

    private Map<String, Object> createSection(String title, String contentKey, Map<String, Object> data) {
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("title", title);
        section.put("contentKey", contentKey);
        if (data != null && data.containsKey(contentKey)) {
            section.put("content", data.get(contentKey));
        }
        return section;
    }

    private List<Map<String, Object>> generateCCERSections() {
        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(Map.of("id", "1", "title", "项目概况", "required", true));
        sections.add(Map.of("id", "2", "title", "项目边界", "required", true));
        sections.add(Map.of("id", "3", "title", "基准线情景", "required", true));
        sections.add(Map.of("id", "4", "title", "项目减排量计算", "required", true));
        sections.add(Map.of("id", "5", "title", "监测计划", "required", true));
        sections.add(Map.of("id", "6", "title", "数据管理", "required", true));
        sections.add(Map.of("id", "7", "title", "补充信息", "required", false));
        return sections;
    }

    private List<Map<String, Object>> generateISO14064Sections() {
        List<Map<String, Object>> sections = new ArrayList<>();
        sections.add(Map.of("id", "1", "title", "组织概述", "required", true));
        sections.add(Map.of("id", "2", "title", "组织边界", "required", true));
        sections.add(Map.of("id", "3", "title", "基准年", "required", true));
        sections.add(Map.of("id", "4", "title", "温室气体排放量化", "required", true));
        sections.add(Map.of("id", "5", "title", "温室气体减排", "required", true));
        sections.add(Map.of("id", "6", "title", "温室气体清单质量管理", "required", true));
        sections.add(Map.of("id", "7", "title", "温室气体报告", "required", true));
        sections.add(Map.of("id", "8", "title", "验证/核查", "required", false));
        return sections;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
