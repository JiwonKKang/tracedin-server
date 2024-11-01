package com.univ.tracedin.infra.span.messaging.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.span.SpanCollectedEvent;
import com.univ.tracedin.domain.span.SpanMessagePublisher;
import com.univ.tracedin.domain.span.TraceId;
import com.univ.tracedin.infra.kafka.KafkaMessageHelper;
import com.univ.tracedin.infra.kafka.KafkaProducer;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpanKafkaEventPublisher implements SpanMessagePublisher {

    private final KafkaProducer<TraceId, SpanCollectedEvent> kafkaProducer;

    @Value("${kafka.topic.span}")
    private String spanTopic;

    @Override
    public void publish(SpanCollectedEvent event) {
        kafkaProducer.send(
                spanTopic, event.getKey(), event, KafkaMessageHelper.getKafkaCallback(event));
    }
}
