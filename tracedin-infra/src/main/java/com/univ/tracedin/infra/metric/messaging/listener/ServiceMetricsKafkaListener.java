package com.univ.tracedin.infra.metric.messaging.listener;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.metric.ServiceMetricsCollectedEvent;
import com.univ.tracedin.domain.metric.ServiceMetricsMessageProcessor;
import com.univ.tracedin.infra.kafka.KafkaConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceMetricsKafkaListener
        implements KafkaConsumer<String, ServiceMetricsCollectedEvent> {

    private final ServiceMetricsMessageProcessor serviceMetricsMessageProcessor;

    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.service-metrics-group-id}",
            topics = "${kafka.topic.service-metrics}")
    public void receive(List<ConsumerRecord<String, ServiceMetricsCollectedEvent>> records) {
        log.info(
                "{} number of service metrics collected events received with keys:{}, partitions:{} and offsets: {}",
                records.size(),
                records.stream().map(ConsumerRecord::key).toList(),
                records.stream().map(ConsumerRecord::partition).toList(),
                records.stream().map(ConsumerRecord::offset).toList());

        final List<ServiceMetricsCollectedEvent> events =
                records.stream().map(ConsumerRecord::value).toList();
        serviceMetricsMessageProcessor.process(events);
    }
}
