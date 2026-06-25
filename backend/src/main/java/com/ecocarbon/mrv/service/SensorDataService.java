package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.IotAlarmRecord;
import com.ecocarbon.mrv.entity.IotAlarmRule;
import com.ecocarbon.mrv.entity.SensorData;
import com.ecocarbon.mrv.repository.IotAlarmRuleRepository;
import com.ecocarbon.mrv.repository.SensorDataRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;
    private final IotAlarmRuleRepository alarmRuleRepository;
    private final IotAlarmService alarmService;
    private final RealtimePushService pushService;

    @Transactional
    public SensorData save(SensorData data) {
        if (data.getCreatedAt() == null) {
            data.setCreatedAt(LocalDateTime.now());
        }
        if (data.getTimestamp() == null) {
            data.setTimestamp(LocalDateTime.now());
        }
        SensorData saved = sensorDataRepository.save(data);
        pushToWebSocket(saved);
        checkAlarm(saved);
        return saved;
    }

    @Transactional
    public List<SensorData> saveAll(List<SensorData> dataList) {
        LocalDateTime now = LocalDateTime.now();
        for (SensorData data : dataList) {
            if (data.getCreatedAt() == null) data.setCreatedAt(now);
            if (data.getTimestamp() == null) data.setTimestamp(now);
        }
        List<SensorData> saved = sensorDataRepository.saveAll(dataList);
        for (SensorData data : saved) {
            pushToWebSocket(data);
            checkAlarm(data);
        }
        return saved;
    }

    public SensorData getLatest(String deviceCode, String metric) {
        return sensorDataRepository.findTopByDeviceCodeAndMetricOrderByTimestampDesc(deviceCode, metric)
                .orElse(null);
    }

    public Page<SensorData> getByTimeRange(String deviceCode, String metric,
                                            LocalDateTime start, LocalDateTime end, Pageable pageable) {
        if (metric != null && !metric.isEmpty()) {
            return sensorDataRepository.findByDeviceCodeAndMetricAndTimestampBetween(
                    deviceCode, metric, start, end, pageable);
        }
        return sensorDataRepository.findByDeviceCodeAndTimestampBetween(deviceCode, start, end, pageable);
    }

    public Map<String, Object> getDeviceLatestAll(String deviceCode) {
        Map<String, Object> result = new HashMap<>();
        String[] metrics = {"temperature", "humidity", "windSpeed", "precipitation",
                "lightIntensity", "co2", "soilTemperature", "soilMoisture",
                "soilPH", "soilConductivity", "ch4", "n2o"};
        for (String metric : metrics) {
            SensorData latest = getLatest(deviceCode, metric);
            if (latest != null) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("value", latest.getValue());
                entry.put("unit", latest.getUnit());
                entry.put("quality", latest.getQuality());
                entry.put("timestamp", latest.getTimestamp());
                result.put(metric, entry);
            }
        }
        return result;
    }

    public Map<String, Object> getStatistics(String deviceCode, String metric,
                                              LocalDateTime start, LocalDateTime end) {
        Object[] stats = sensorDataRepository.getStatistics(deviceCode, metric, start, end);
        Map<String, Object> result = new HashMap<>();
        if (stats != null && stats.length == 4) {
            result.put("min", stats[0]);
            result.put("max", stats[1]);
            result.put("avg", stats[2]);
            result.put("count", stats[3]);
        }
        result.put("deviceCode", deviceCode);
        result.put("metric", metric);
        return result;
    }

    private void pushToWebSocket(SensorData data) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceCode", data.getDeviceCode());
            payload.put("deviceType", data.getDeviceType());
            payload.put("metric", data.getMetric());
            payload.put("value", data.getValue());
            payload.put("unit", data.getUnit());
            payload.put("quality", data.getQuality());
            payload.put("timestamp", data.getTimestamp().toString());
            pushService.pushDeviceData(data.getDeviceCode(), payload);
        } catch (Exception e) {
            log.warn("推送传感器数据失败: {}", e.getMessage());
        }
    }

    private void checkAlarm(SensorData data) {
        try {
            List<IotAlarmRule> rules = alarmRuleRepository.findByEnabled(1);
            for (IotAlarmRule rule : rules) {
                if (!matchesRule(data, rule)) continue;
                if (!evaluateCondition(data.getValue(), rule.getCondition(),
                        rule.getThresholdValue(), rule.getThresholdValue2())) continue;
                IotAlarmRecord record = new IotAlarmRecord();
                record.setRuleId(rule.getId());
                record.setDeviceCode(data.getDeviceCode());
                record.setMetric(data.getMetric());
                record.setActualValue(data.getValue());
                record.setThresholdValue(rule.getThresholdValue());
                record.setSeverity(rule.getSeverity());
                alarmService.createRecord(record);
                Map<String, Object> alarmPayload = new HashMap<>();
                alarmPayload.put("ruleName", rule.getRuleName());
                alarmPayload.put("deviceCode", data.getDeviceCode());
                alarmPayload.put("metric", data.getMetric());
                alarmPayload.put("actualValue", data.getValue());
                alarmPayload.put("thresholdValue", rule.getThresholdValue());
                alarmPayload.put("severity", rule.getSeverity());
                pushService.pushAlarm(data.getDeviceCode(), alarmPayload);
            }
        } catch (Exception e) {
            log.warn("告警检查失败: {}", e.getMessage());
        }
    }

    private boolean matchesRule(SensorData data, IotAlarmRule rule) {
        if (rule.getMetric() != null && !rule.getMetric().equals(data.getMetric())) return false;
        if (rule.getDeviceCode() != null && !rule.getDeviceCode().isEmpty()
                && !rule.getDeviceCode().equals(data.getDeviceCode())) return false;
        if (rule.getDeviceType() != null && !rule.getDeviceType().isEmpty()
                && !rule.getDeviceType().equals(data.getDeviceType())) return false;
        return true;
    }

    private boolean evaluateCondition(Double value, String condition, Double threshold, Double threshold2) {
        if (value == null || condition == null || threshold == null) return false;
        return switch (condition) {
            case "GT" -> value > threshold;
            case "LT" -> value < threshold;
            case "EQ" -> Math.abs(value - threshold) < 0.001;
            case "BETWEEN" -> threshold2 != null && value >= threshold && value <= threshold2;
            case "GTE" -> value >= threshold;
            case "LTE" -> value <= threshold;
            default -> false;
        };
    }
}
