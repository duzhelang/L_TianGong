package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.LandUseData;
import com.ecocarbon.mrv.repository.LandUseDataRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LandUseChangeService {
    private final LandUseDataRepository landUseDataRepository;

    public Map<String, Object> detectChanges(String areaId, int year1, int year2) {
        List<LandUseData> data1 = landUseDataRepository.findByAreaIdAndYear(areaId, year1);
        List<LandUseData> data2 = landUseDataRepository.findByAreaIdAndYear(areaId, year2);

        Map<String, Double> map1 = data1.stream()
                .collect(Collectors.toMap(LandUseData::getLandType, d -> d.getAreaHa() != null ? d.getAreaHa() : 0.0, (v1, v2) -> v1));
        Map<String, Double> map2 = data2.stream()
                .collect(Collectors.toMap(LandUseData::getLandType, d -> d.getAreaHa() != null ? d.getAreaHa() : 0.0, (v1, v2) -> v1));

        List<Map<String, Object>> changes = new ArrayList<>();
        Set<String> allTypes = new HashSet<>();
        allTypes.addAll(map1.keySet());
        allTypes.addAll(map2.keySet());

        double totalChange = 0;
        for (String type : allTypes) {
            double v1 = map1.getOrDefault(type, 0.0);
            double v2 = map2.getOrDefault(type, 0.0);
            double change = v2 - v1;
            double pct = v1 > 0 ? (change / v1) * 100 : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("landType", type);
            item.put("year1Area", v1);
            item.put("year2Area", v2);
            item.put("change", Math.round(change * 100.0) / 100.0);
            item.put("changePercent", Math.round(pct * 100.0) / 100.0);
            changes.add(item);
            totalChange += Math.abs(change);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("areaId", areaId);
        result.put("year1", year1);
        result.put("year2", year2);
        result.put("changes", changes);
        result.put("totalAbsoluteChange", Math.round(totalChange * 100.0) / 100.0);
        return result;
    }

    public List<Map<String, Object>> getTimeSeries(String areaId) {
        List<LandUseData> allData = landUseDataRepository.findByAreaId(areaId);
        Map<Integer, List<LandUseData>> byYear = allData.stream()
                .collect(Collectors.groupingBy(LandUseData::getYear));

        List<Map<String, Object>> series = new ArrayList<>();
        for (Map.Entry<Integer, List<LandUseData>> entry : byYear.entrySet()) {
            Map<String, Object> yearData = new HashMap<>();
            yearData.put("year", entry.getKey());
            for (LandUseData d : entry.getValue()) {
                yearData.put(d.getLandType(), d.getAreaHa());
            }
            series.add(yearData);
        }
        series.sort((a, b) -> Integer.compare((int) a.get("year"), (int) b.get("year")));
        return series;
    }

    public Map<String, Object> getTransferMatrix(String areaId, int year1, int year2) {
        Map<String, Object> result = new HashMap<>();
        result.put("areaId", areaId);
        result.put("year1", year1);
        result.put("year2", year2);
        result.put("note", "土地利用转移矩阵需要详细的地块级数据，当前仅提供面积变化统计");
        result.put("changes", detectChanges(areaId, year1, year2).get("changes"));
        return result;
    }
}
