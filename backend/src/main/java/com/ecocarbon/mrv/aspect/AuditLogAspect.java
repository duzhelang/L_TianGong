package com.ecocarbon.mrv.aspect;

import com.ecocarbon.mrv.service.SysAuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    private final SysAuditLogService auditLogService;

    @Pointcut("execution(* com.ecocarbon.mrv.controller..*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        String params = Arrays.toString(joinPoint.getArgs());
        String ip = getClientIp();
        Long userId = null;
        String username = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                userId = auth.getPrincipal() instanceof Long ? (Long) auth.getPrincipal() : null;
                username = auth.getDetails() instanceof String ? (String) auth.getDetails() : null;
            }
        } catch (Exception ignored) {
        }
        int status = 1;
        String errorMsg = null;
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            status = 0;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String operation = extractOperation(method);
            try {
                auditLogService.record(userId, username, operation, method, params, ip, duration, status, errorMsg);
            } catch (Exception e) {
                log.warn("记录审计日志失败: {}", e.getMessage());
            }
        }
    }

    private String getClientIp() {
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
                return ip;
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }

    private String extractOperation(String method) {
        if (method.contains("create") || method.contains("add") || method.contains("register")) {
            return "CREATE";
        } else if (method.contains("update") || method.contains("edit")) {
            return "UPDATE";
        } else if (method.contains("delete") || method.contains("remove")) {
            return "DELETE";
        } else if (method.contains("login")) {
            return "LOGIN";
        } else if (method.contains("logout")) {
            return "LOGOUT";
        }
        return "QUERY";
    }
}
