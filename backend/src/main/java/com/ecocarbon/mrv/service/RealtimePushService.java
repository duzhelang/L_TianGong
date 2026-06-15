package com.ecocarbon.mrv.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealtimePushService {
    private final SimpMessagingTemplate messagingTemplate;

    public void pushDeviceData(String deviceCode, Map<String, Object> data) {
        try {
            messagingTemplate.convertAndSend("/topic/device/" + deviceCode, data);
        } catch (Exception e) {
            log.warn("推送设备数据失败: {}", e.getMessage());
        }
    }

    public void pushProjectData(String projectId, Map<String, Object> data) {
        try {
            messagingTemplate.convertAndSend("/topic/project/" + projectId, data);
        } catch (Exception e) {
            log.warn("推送项目数据失败: {}", e.getMessage());
        }
    }

    public void pushAlarm(String deviceCode, Map<String, Object> alarm) {
        try {
            messagingTemplate.convertAndSend("/topic/alarm/" + deviceCode, alarm);
            messagingTemplate.convertAndSend("/topic/alarm", alarm);
        } catch (Exception e) {
            log.warn("推送告警失败: {}", e.getMessage());
        }
    }

    public void broadcast(Map<String, Object> message) {
        try {
            messagingTemplate.convertAndSend("/topic/broadcast", message);
        } catch (Exception e) {
            log.warn("广播消息失败: {}", e.getMessage());
        }
    }
}
