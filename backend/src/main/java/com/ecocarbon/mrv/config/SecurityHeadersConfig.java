package com.ecocarbon.mrv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain filterChain) 
                    throws ServletException, IOException {
                
                // 防止点击劫持
                response.setHeader("X-Frame-Options", "DENY");
                
                // 启用XSS过滤
                response.setHeader("X-XSS-Protection", "1; mode=block");
                
                // 防止MIME类型嗅探
                response.setHeader("X-Content-Type-Options", "nosniff");
                
                // 严格传输安全
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                
                // 内容安全策略
                response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:");
                
                // 引用策略
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                
                // 权限策略
                response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                
                filterChain.doFilter(request, response);
            }
        };
    }
}
