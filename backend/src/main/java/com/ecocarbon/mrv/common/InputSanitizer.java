package com.ecocarbon.mrv.common;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script[^>]*>.*?</script>");
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(?i)(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER)\\b)");

    public String sanitize(String input) {
        if (input == null) {
            return null;
        }

        String sanitized = input;

        // 移除脚本标签
        sanitized = SCRIPT_PATTERN.matcher(sanitized).replaceAll("");

        // 移除HTML标签
        sanitized = HTML_PATTERN.matcher(sanitized).replaceAll("");

        // 转义特殊字符
        sanitized = sanitized
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");

        return sanitized;
    }

    public boolean containsSqlInjection(String input) {
        if (input == null) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public boolean containsXss(String input) {
        if (input == null) {
            return false;
        }
        return SCRIPT_PATTERN.matcher(input).find() || HTML_PATTERN.matcher(input).find();
    }
}
