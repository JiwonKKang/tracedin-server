package com.univ.tracedin.infra.metric.messaging.publish;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.metric.ServiceMetricsCollectedEvent;
import com.univ.tracedin.domain.metric.ServiceMetricsMessagePublisher;
import com.univ.tracedin.infra.kafka.KafkaMessageHelper;
import com.univ.tracedin.infra.kafka.KafkaProducer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceMetricsKafkaPublisher implements ServiceMetricsMessagePublisher {

    private final KafkaProducer<String, ServiceMetricsCollectedEvent> kafkaProducer;

    @Value("${kafka.topic.service-metrics}")
    private String serviceMetricsTopic;

    @Override
    public void publish(ServiceMetricsCollectedEvent serviceMetricsCollectedEvent) {
        kafkaProducer.send(
                serviceMetricsTopic,
                serviceMetricsCollectedEvent.getKey(),
                serviceMetricsCollectedEvent,
                KafkaMessageHelper.getKafkaCallback(serviceMetricsCollectedEvent));
    }
}
