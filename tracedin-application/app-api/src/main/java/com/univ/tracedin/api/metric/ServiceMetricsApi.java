package com.univ.tracedin.api.metric;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.api.global.sse.SseConnector;
import com.univ.tracedin.domain.project.NetworkTopology.Node;
import com.univ.tracedin.domain.project.ProjectKey;

@RestController
@RequestMapping("/api/v1/service-metrics")
@RequiredArgsConstructor
public class ServiceMetricsApi implements ServiceMetricsApiDocs {

    private final SseConnector<Node> sseConnector;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(String projectKey, String serviceName) {
        SseEmitter emitter =
                sseConnector.connect(Node.createService(ProjectKey.from(projectKey), serviceName));
        return ResponseEntity.ok(emitter);
    }
}
