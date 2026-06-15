package com.ecocarbon.mrv.aspect;

import com.ecocarbon.mrv.config.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final StringRedisTemplate redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildKey(rateLimit);
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        try {
            Long current = redisTemplate.opsForValue().increment(redisKey);
            if (current != null && current == 1) {
                redisTemplate.expire(redisKey, rateLimit.timeWindowSeconds(), TimeUnit.SECONDS);
            }
            if (current != null && current > rateLimit.maxRequests()) {
                throw new RuntimeException("请求过于频繁，请稍后再试");
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("请求过于频繁")) {
                throw e;
            }
            log.warn("Redis限流检查失败，降级放行: {}", e.getMessage());
        }
        return joinPoint.proceed();
    }

    private String buildKey(RateLimit rateLimit) {
        String key = rateLimit.key();
        if (key.isEmpty()) {
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String ip = request.getHeader("X-Forwarded-For");
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getHeader("X-Real-IP");
                    }
                    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                        ip = request.getRemoteAddr();
                    }
                    key = ip + ":" + request.getRequestURI();
                }
            } catch (Exception e) {
                key = "default";
            }
        }
        return key;
    }
}
