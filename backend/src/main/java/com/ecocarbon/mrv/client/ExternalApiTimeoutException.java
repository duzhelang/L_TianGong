package com.ecocarbon.mrv.client;

/**
 * 外部API调用超时异常
 * 当API调用超过配置的超时时间时抛出
 */
public class ExternalApiTimeoutException extends ExternalApiException {

    public ExternalApiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
