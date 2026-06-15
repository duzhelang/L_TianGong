package com.ecocarbon.mrv.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @BelongsProject: Software-CUCN
 * @BelongsPackage: com.ecocarbon.mrv.config
 * @Author: DZL-125
 * @CreateTime: 2026/4/21 上午12:20
 * @Version: 1.0
 */
@Component
public class PortAutoConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Value("${server.port:8080}")
    private int startPort;

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        // 如果配置为 0（随机端口），跳过处理
        if (startPort == 0) {
            return;
        }

        int port = startPort;
        int maxPort = startPort + 100; // 最多往后找 100 个端口，防止无限循环

        while (port < maxPort) {
            if (!isPortInUse(port)) {
                factory.setPort(port);
                System.out.println("✅ 服务启动在端口: " + port);
                return;
            }
            System.out.println("⚠️ 端口 " + port + " 被占用，尝试下一个...");
            port++;
        }

        throw new RuntimeException("在 [" + startPort + ", " + maxPort + ") 范围内未找到可用端口");
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return false; // 端口可用
        } catch (IOException e) {
            return true;  // 端口被占用
        }
    }
}