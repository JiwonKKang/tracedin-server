package com.univ.tracedin.api.global.sse;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.metric.EventSender;
import com.univ.tracedin.domain.metric.ServiceMetrics;

@Component
@RequiredArgsConstructor
public class SseSender implements EventSender {

    private static final String METRICS_POSTFIX = " - metrics";
    private final SseEmitterRepository sseEmitterRepository;

    @Override
    public void send(ServiceMetrics serviceMetrics) {
        sseEmitterRepository
                .findById(serviceMetrics.toNode())
                .ifPresent(
                        emitter -> {
                            try {
                                emitter.send(
                                        SseEmitter.event()
                                                .id("")
                                                .name(
                                                        serviceMetrics.toNode().getName()
                                                                + METRICS_POSTFIX)
                                                .data(serviceMetrics.getMetrics()));
                            } catch (IOException e) {
                                sseEmitterRepository.remove(serviceMetrics.toNode());
                                emitter.completeWithError(e);
                            }
                        });
    }
}
