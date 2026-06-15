package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.IotAlarmRecord;
import com.ecocarbon.mrv.entity.IotAlarmRule;
import com.ecocarbon.mrv.repository.IotAlarmRecordRepository;
import com.ecocarbon.mrv.repository.IotAlarmRuleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IotAlarmService {
    private final IotAlarmRuleRepository ruleRepository;
    private final IotAlarmRecordRepository recordRepository;

    public List<IotAlarmRule> listRules() {
        return ruleRepository.findAll();
    }

    public IotAlarmRule getRuleById(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("告警规则不存在"));
    }

    @Transactional
    public IotAlarmRule createRule(IotAlarmRule rule) {
        rule.setCreatedAt(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    @Transactional
    public IotAlarmRule updateRule(Long id, IotAlarmRule rule) {
        IotAlarmRule existing = getRuleById(id);
        existing.setRuleName(rule.getRuleName());
        existing.setDeviceType(rule.getDeviceType());
        existing.setDeviceCode(rule.getDeviceCode());
        existing.setMetric(rule.getMetric());
        existing.setCondition(rule.getCondition());
        existing.setThresholdValue(rule.getThresholdValue());
        existing.setThresholdValue2(rule.getThresholdValue2());
        existing.setSeverity(rule.getSeverity());
        existing.setEnabled(rule.getEnabled());
        return ruleRepository.save(existing);
    }

    @Transactional
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    public Page<IotAlarmRecord> listRecords(Pageable pageable) {
        return recordRepository.findAll(pageable);
    }

    public Page<IotAlarmRecord> getActiveAlarms(Pageable pageable) {
        return recordRepository.findByStatus("ACTIVE", pageable);
    }

    public List<IotAlarmRecord> getDeviceAlarms(String deviceCode) {
        return recordRepository.findByDeviceCode(deviceCode);
    }

    @Transactional
    public IotAlarmRecord createRecord(IotAlarmRecord record) {
        record.setStatus("ACTIVE");
        record.setCreatedAt(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Transactional
    public IotAlarmRecord acknowledge(Long id, Long userId) {
        IotAlarmRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("告警记录不存在"));
        record.setStatus("ACKNOWLEDGED");
        record.setAcknowledgedBy(userId);
        record.setAcknowledgedAt(LocalDateTime.now());
        return recordRepository.save(record);
    }

    @Transactional
    public IotAlarmRecord resolve(Long id) {
        IotAlarmRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("告警记录不存在"));
        record.setStatus("RESOLVED");
        record.setResolvedAt(LocalDateTime.now());
        return recordRepository.save(record);
    }

    public long getActiveAlarmCount() {
        return recordRepository.countByStatus("ACTIVE");
    }
}
