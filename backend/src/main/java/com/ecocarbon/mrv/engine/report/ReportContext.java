package com.ecocarbon.mrv.engine.report;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportContext {
    private Long projectId;
    private String projectName;
    private String sceneType;
    private String reportType;
    private Map<String, Object> emissionData;
    private Map<String, Object> projectInfo;
    private String methodology;
    private String calculationProcess;
}
