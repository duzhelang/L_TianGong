package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.service.RealtimePushService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final RealtimePushService pushService;

    @MessageMapping("/device/data")
    @SendTo("/topic/device/data")
    public Map<String, Object> handleDeviceData(Map<String, Object> data) {
        return data;
    }

    @MessageMapping("/heartbeat")
    @SendTo("/topic/heartbeat")
    public Map<String, Object> handleHeartbeat(Map<String, Object> data) {
        return Map.of("type", "heartbeat", "timestamp", System.currentTimeMillis());
    }
}
