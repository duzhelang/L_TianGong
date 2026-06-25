package com.ecocarbon.mrv.client;

import lombok.Getter;

/**
 * 外部API调用异常基类
 * 用于封装外部API调用过程中的各类异常，保留原始异常信息
 */
@Getter
public class ExternalApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public ExternalApiException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ExternalApiException(String message, Throwable cause) {
        this(message, cause, -1, null);
    }

    public ExternalApiException(String message) {
        this(message, null, -1, null);
    }
}
