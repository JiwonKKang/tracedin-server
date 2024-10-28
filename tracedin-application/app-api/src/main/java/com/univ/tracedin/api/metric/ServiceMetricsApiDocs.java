package com.univ.tracedin.api.metric;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "메트릭 API")
public interface ServiceMetricsApiDocs {

    @Operation(summary = "메트릭 SSE 구독", description = "SSE 이벤트를 구독하여 메트릭을 실시간으로 받을 수 있습니다.")
    ResponseEntity<SseEmitter> subscribe(String projectKey, String serviceName);
}
