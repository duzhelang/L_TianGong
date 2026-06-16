package com.ecocarbon.mrv.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
public class ExternalApiClient {

    private final ObjectMapper objectMapper;
    private final int timeoutSeconds;

    public ExternalApiClient(ObjectMapper objectMapper,
                             @Value("${external-api.timeout:30}") int timeoutSeconds) {
        this.objectMapper = objectMapper;
        this.timeoutSeconds = timeoutSeconds;
    }

    public WebClient createWebClient(String baseUrl, int timeoutSeconds) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public WebClient createWebClientWithApiKey(String baseUrl, String apiKey, int timeoutSeconds) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("apikey", apiKey)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public JsonNode get(WebClient client, String path, Map<String, String> params) {
        WebClient.RequestHeadersSpec<?> request = client.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    if (params != null) {
                        params.forEach(uriBuilder::queryParam);
                    }
                    return uriBuilder.build();
                });

        return executeRequest(() -> request.retrieve().bodyToMono(String.class), "GET");
    }

    public JsonNode getWithAuth(WebClient client, String path, Map<String, String> params, String authHeader) {
        WebClient.RequestHeadersSpec<?> request = client.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    if (params != null) {
                        params.forEach(uriBuilder::queryParam);
                    }
                    return uriBuilder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, authHeader);

        return executeRequest(() -> request.retrieve().bodyToMono(String.class), "GET");
    }

    public JsonNode post(WebClient client, String path, Object body) {
        return executeRequest(
                () -> client.post()
                        .uri(path)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(String.class),
                "POST"
        );
    }

    /**
     * 统一的请求执行方法，封装异常处理逻辑
     */
    private JsonNode executeRequest(Supplier<Mono<String>> requestSupplier, String method) {
        try {
            String response = requestSupplier.get()
                    .block(Duration.ofSeconds(timeoutSeconds));
            return objectMapper.readTree(response);
        } catch (WebClientResponseException e) {
            log.error("外部API {} 调用失败: {} - {}", method, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException(
                    "外部API " + method + " 调用失败: " + e.getMessage(),
                    e, e.getStatusCode().value(), e.getResponseBodyAsString()
            );
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("Timeout")) {
                log.error("外部API {} 调用超时: {}", method, e.getMessage());
                throw new ExternalApiTimeoutException("外部API " + method + " 调用超时", e);
            }
            log.error("外部API {} 调用异常: {}", method, e.getMessage());
            throw new ExternalApiException("外部API " + method + " 调用异常", e);
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("外部API {} 调用异常: {}", method, e.getMessage());
            throw new ExternalApiException("外部API " + method + " 调用异常", e);
        }
    }
}
