package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.LandUseData;
import com.ecocarbon.mrv.entity.NPPData;
import com.ecocarbon.mrv.entity.RemoteSensingIndex;
import com.ecocarbon.mrv.repository.LandUseDataRepository;
import com.ecocarbon.mrv.repository.NPPDataRepository;
import com.ecocarbon.mrv.repository.RemoteSensingIndexRepository;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoteSensingService {
    private final RemoteSensingIndexRepository remoteSensingIndexRepository;
    private final NPPDataRepository nppDataRepository;
    private final LandUseDataRepository landUseDataRepository;

    public List<RemoteSensingIndex> getNDVIData(String areaId, LocalDate startDate, LocalDate endDate) {
        return remoteSensingIndexRepository.findByAreaIdAndIndexTypeAndCaptureDateBetween(
                areaId, "NDVI", startDate, endDate);
    }

    public List<RemoteSensingIndex> getIndexTimeSeries(String areaId, String indexType) {
        return remoteSensingIndexRepository.findByAreaIdAndIndexType(areaId, indexType);
    }

    public List<RemoteSensingIndex> getIndexData(String areaId, String indexType, LocalDate startDate, LocalDate endDate) {
        return remoteSensingIndexRepository.findByAreaIdAndIndexTypeAndCaptureDateBetween(
                areaId, indexType, startDate, endDate);
    }

    public List<NPPData> getNPPTimeSeries(String areaId, Integer startYear, Integer endYear) {
        return nppDataRepository.findByAreaIdAndYearBetween(areaId, startYear, endYear);
    }

    public List<NPPData> getNPPByYear(String areaId, Integer year) {
        return nppDataRepository.findByAreaIdAndYear(areaId, year);
    }

    public List<NPPData> getNPPByMonth(String areaId, Integer year, Integer month) {
        return nppDataRepository.findByAreaIdAndYearAndMonth(areaId, year, month);
    }

    public List<LandUseData> getLandUseData(String areaId, Integer year) {
        return landUseDataRepository.findByAreaIdAndYear(areaId, year);
    }

    public List<LandUseData> getLandUseByType(String areaId, String landType) {
        return landUseDataRepository.findByAreaIdAndLandType(areaId, landType);
    }

    public Map<String, Object> getIndexStatistics(String areaId, String indexType) {
        List<RemoteSensingIndex> indices = remoteSensingIndexRepository.findByAreaIdAndIndexType(areaId, indexType);
        if (indices.isEmpty()) {
            return Map.of();
        }

        DoubleSummaryStatistics stats = indices.stream()
                .mapToDouble(RemoteSensingIndex::getValue)
                .summaryStatistics();

        Map<String, Object> result = new HashMap<>();
        result.put("count", stats.getCount());
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        result.put("average", stats.getAverage());
        result.put("sum", stats.getSum());
        return result;
    }

    public Map<String, Object> getNPPStatistics(String areaId, Integer year) {
        List<NPPData> nppList = nppDataRepository.findByAreaIdAndYear(areaId, year);
        if (nppList.isEmpty()) {
            return Map.of();
        }

        DoubleSummaryStatistics stats = nppList.stream()
                .mapToDouble(NPPData::getNppValue)
                .summaryStatistics();

        Map<String, Object> result = new HashMap<>();
        result.put("count", stats.getCount());
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        result.put("average", stats.getAverage());
        result.put("totalNPP", stats.getSum());
        return result;
    }

    public Map<String, Object> getLandUseSummary(String areaId, Integer year) {
        List<LandUseData> landUseList = landUseDataRepository.findByAreaIdAndYear(areaId, year);
        if (landUseList.isEmpty()) {
            return Map.of();
        }

        Map<String, Double> typeDistribution = new HashMap<>();
        double totalArea = 0.0;
        for (LandUseData landUse : landUseList) {
            typeDistribution.merge(landUse.getLandType(), landUse.getAreaHa(), Double::sum);
            totalArea += landUse.getAreaHa();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalArea", totalArea);
        result.put("typeDistribution", typeDistribution);
        result.put("typeCount", typeDistribution.size());
        return result;
    }
}
