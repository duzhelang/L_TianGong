package com.ecocarbon.mrv.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "external.api")
public class ApiProperties {

    private Weather weather = new Weather();
    private EmissionFactor emissionFactor = new EmissionFactor();
    private RemoteSensing remoteSensing = new RemoteSensing();
    private Soil soil = new Soil();
    private Energy energy = new Energy();

    @Data
    public static class Weather {
        private String baseUrl = "https://devapi.qweather.com";
        private String apiKey;
        private int timeoutSeconds = 10;
    }

    @Data
    public static class EmissionFactor {
        private String baseUrl = "https://emf.epa.gov";
        private String apiKey;
        private int timeoutSeconds = 15;
    }

    @Data
    public static class RemoteSensing {
        private String baseUrl = "https://appeears.earthdatacloud.nasa.gov";
        private String apiKey;
        private int timeoutSeconds = 30;
    }

    @Data
    public static class Soil {
        private String baseUrl = "https://rest.isric.org";
        private String apiKey;
        private int timeoutSeconds = 20;
    }

    @Data
    public static class Energy {
        private String baseUrl;
        private String apiKey;
        private int timeoutSeconds = 10;
    }
}