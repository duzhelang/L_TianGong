package com.ecocarbon.mrv.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiProviderConfig {

    private boolean enabled = false;
    private String defaultProvider = "openrouter";
    private int timeoutSeconds = 30;
    private Map<String, ProviderConfig> providers;

    @Data
    public static class ProviderConfig {
        private String baseUrl;
        private String apiKey;
        private Map<String, String> models;
        private String defaultModel;
    }
}
