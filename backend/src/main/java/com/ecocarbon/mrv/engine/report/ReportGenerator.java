package com.ecocarbon.mrv.engine.report;

import java.util.Map;

public interface ReportGenerator {
    String getType();
    byte[] generate(ReportContext context);
}
