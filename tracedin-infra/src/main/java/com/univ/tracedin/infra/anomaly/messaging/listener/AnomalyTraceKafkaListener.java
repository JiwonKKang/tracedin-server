package com.univ.tracedin.infra.anomaly.messaging.listener;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.anomaly.AnomalyTrace;
import com.univ.tracedin.domain.anomaly.AnomalyTraceProcessor;
import com.univ.tracedin.domain.span.TraceId;
import com.univ.tracedin.infra.anomaly.client.AnomalyTraceResult;
import com.univ.tracedin.infra.kafka.KafkaConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnomalyTraceKafkaListener implements KafkaConsumer<TraceId, AnomalyTraceResult> {

    private final AnomalyTraceProcessor anomalyTraceProcessor;

    @Override
    @KafkaListener(
            id = "${kafka-consumer-config.anomaly-trace-group-id}",
            topics = "${kafka.topic.anomaly-trace}")
    public void receive(List<ConsumerRecord<TraceId, AnomalyTraceResult>> records) {
        log.info(
                "{} number of anomaly trace events received with keys:{}, partitions:{} and offsets: {}",
                records.size(),
                records.stream().map(ConsumerRecord::key).toList(),
                records.stream().map(ConsumerRecord::partition).toList(),
                records.stream().map(ConsumerRecord::offset).toList());

        final List<AnomalyTrace> anomalyTraces =
                records.stream()
                        .map(
                                record ->
                                        AnomalyTrace.from(
                                                record.key(),
                                                record.value().projectKey(),
                                                record.value().anomalySpanIds()))
                        .toList();
        anomalyTraceProcessor.process(anomalyTraces);
    }
}
