package com.univ.tracedin.api.global.sse;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseConnector<T> {

    public static final String CONNECTION_NAME = "CONNECT";
    public static final long TIMEOUT = 30 * 60 * 1000L;

    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter connect(T key) {
        final SseEmitter emitter = new SseEmitter(TIMEOUT);
        sseEmitterRepository.save(key, emitter);
        emitter.onTimeout(() -> sseEmitterRepository.remove(key));
        emitter.onCompletion(() -> sseEmitterRepository.remove(key));

        try {
            emitter.send(SseEmitter.event().id("").name(CONNECTION_NAME).data("emitter connected"));
        } catch (IOException e) {
            sseEmitterRepository.remove(key);
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
