package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.SysAuditLog;
import com.ecocarbon.mrv.repository.SysAuditLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysAuditLogService {
    private final SysAuditLogRepository auditLogRepository;

    public SysAuditLog record(Long userId, String username, String operation,
                              String method, String params, String ip,
                              long duration, int status, String errorMsg) {
        SysAuditLog log = new SysAuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        log.setDuration(duration);
        log.setStatus(status);
        log.setErrorMsg(errorMsg);
        log.setCreatedAt(LocalDateTime.now());
        return auditLogRepository.save(log);
    }

    public Page<SysAuditLog> list(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<SysAuditLog> getByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    public Page<SysAuditLog> getByOperation(String operation, Pageable pageable) {
        return auditLogRepository.findByOperation(operation, pageable);
    }

    public List<SysAuditLog> getByTimeRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByCreatedAtBetween(start, end);
    }
}
