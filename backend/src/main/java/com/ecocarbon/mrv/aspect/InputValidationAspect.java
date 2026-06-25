package com.ecocarbon.mrv.aspect;

import com.ecocarbon.mrv.common.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class InputValidationAspect {

    private final InputSanitizer inputSanitizer;

    private static final int MAX_DEPTH = 10;

    @Around("execution(* com.ecocarbon.mrv.controller.*.*(..))")
    public Object validateInput(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            validateObject(arg, 0, new IdentityHashMap<>());
        }

        return joinPoint.proceed();
    }

    private void validateObject(Object obj, int depth, Map<Object, Boolean> visited) {
        if (obj == null || depth > MAX_DEPTH) {
            return;
        }

        if (visited.containsKey(obj)) {
            return;
        }
        visited.put(obj, Boolean.TRUE);

        if (obj instanceof String) {
            validateString((String) obj);
        } else if (obj instanceof Collection) {
            for (Object item : (Collection<?>) obj) {
                validateObject(item, depth + 1, visited);
            }
        } else if (obj instanceof Map) {
            for (Object value : ((Map<?, ?>) obj).values()) {
                validateObject(value, depth + 1, visited);
            }
        } else if (!isSimpleType(obj.getClass())) {
            validateFields(obj, depth, visited);
        }
    }

    private void validateFields(Object obj, int depth, Map<Object, Boolean> visited) {
        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    validateObject(value, depth + 1, visited);
                } catch (IllegalAccessException e) {
                    log.debug("无法访问字段: {}.{}", clazz.getSimpleName(), field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private void validateString(String value) {
        if (inputSanitizer.containsSqlInjection(value)) {
            log.warn("检测到SQL注入尝试: {}", value);
            throw new IllegalArgumentException("输入包含非法字符");
        }
        if (inputSanitizer.containsXss(value)) {
            log.warn("检测到XSS攻击尝试: {}", value);
            throw new IllegalArgumentException("输入包含非法字符");
        }
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Boolean.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Character.class
                || clazz.isEnum()
                || clazz.getPackageName().startsWith("java.");
    }
}
