package com.univ.tracedin.infra.metric.messaging.listener;

import java.io.IOException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.univ.tracedin.domain.metric.EventSender;
import com.univ.tracedin.domain.metric.ServiceMetrics;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceMetricsRedisListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final EventSender eventSender;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            final ServiceMetrics serviceMetrics =
                    objectMapper.readValue(message.getBody(), ServiceMetrics.class);
            log.info("Service metrics received: {}", serviceMetrics);
            eventSender.send(serviceMetrics);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
