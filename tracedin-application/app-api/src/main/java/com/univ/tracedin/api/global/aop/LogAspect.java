package com.univ.tracedin.api.global.aop;

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class LogAspect {

    private static final Pattern PATTERN = Pattern.compile("\\.");

    @Pointcut(
            "execution(* com.univ.tracedin.domain..*(..)) || execution(* com.univ.tracedin.infra..*(..)) && !execution(* com.univ.tracedin.common..*(..))")
    public void all() {}

    @Pointcut(
            "execution(* com.univ.tracedin.api..*Api.*(..)) && !execution(* com.univ.tracedin.api.HealthCheckApi..*(..))")
    public void controller() {}

    @Around("all()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            final long end = System.currentTimeMillis();
            final long timeinMs = end - start;
            log.info("{} | time = {}ms", joinPoint.getSignature(), timeinMs);
        }
    }

    @Around("controller()")
    public Object loggingBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        final HttpServletRequest request =
                ((ServletRequestAttributes)
                                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest();

        final String controllerName = joinPoint.getSignature().getDeclaringType().getName();
        final String methodName = joinPoint.getSignature().getName();
        final Map<String, Object> params = new HashMap<>();

        try {
            final String decodedURI = URLDecoder.decode(request.getRequestURI(), "UTF-8");

            params.put("controller", controllerName);
            params.put("method", methodName);
            params.put("params", getParams(request));
            params.put("log_time", System.currentTimeMillis());
            params.put("request_uri", decodedURI);
            params.put("http_method", request.getMethod());
        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        }

        log.info("[{}] {}", params.get("http_method"), params.get("request_uri"));
        log.info("method: {}.{}", params.get("controller"), params.get("method"));
        log.info("params: {}", params.get("params"));

        return joinPoint.proceed();
    }

    private static JSONObject getParams(HttpServletRequest request) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            final String param = params.nextElement();
            final String replaceParam = PATTERN.matcher(param).replaceAll("-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }
}
