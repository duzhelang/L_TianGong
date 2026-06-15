package com.ecocarbon.mrv.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class ExternalApiClient {

    private final ObjectMapper objectMapper;

    public ExternalApiClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WebClient createWebClient(String baseUrl, int timeoutSeconds) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    public WebClient createWebClientWithApiKey(String baseUrl, String apiKey, int timeoutSeconds) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", apiKey)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    public JsonNode get(WebClient client, String path, Map<String, String> params) {
        try {
            WebClient.RequestHeadersSpec<?> request = client.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(path);
                        if (params != null) {
                            params.forEach(uriBuilder::queryParam);
                        }
                        return uriBuilder.build();
                    });

            String response = request.retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            return objectMapper.readTree(response);
        } catch (WebClientResponseException e) {
            log.error("外部API调用失败: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("外部API调用失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("外部API调用异常: {}", e.getMessage());
            throw new RuntimeException("外部API调用异常", e);
        }
    }

    public JsonNode getWithAuth(WebClient client, String path, Map<String, String> params, String authHeader) {
        try {
            WebClient.RequestHeadersSpec<?> request = client.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(path);
                        if (params != null) {
                            params.forEach(uriBuilder::queryParam);
                        }
                        return uriBuilder.build();
                    })
                    .header(HttpHeaders.AUTHORIZATION, authHeader);

            String response = request.retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            return objectMapper.readTree(response);
        } catch (WebClientResponseException e) {
            log.error("外部API调用失败: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("外部API调用失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("外部API调用异常: {}", e.getMessage());
            throw new RuntimeException("外部API调用异常", e);
        }
    }

    public JsonNode post(WebClient client, String path, Object body) {
        try {
            String response = client.post()
                    .uri(path)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(30));

            return objectMapper.readTree(response);
        } catch (WebClientResponseException e) {
            log.error("外部API POST调用失败: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("外部API POST调用失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("外部API POST调用异常: {}", e.getMessage());
            throw new RuntimeException("外部API POST调用异常", e);
        }
    }
}