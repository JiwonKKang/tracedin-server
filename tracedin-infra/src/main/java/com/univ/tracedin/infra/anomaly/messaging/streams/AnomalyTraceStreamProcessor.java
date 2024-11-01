package com.univ.tracedin.infra.anomaly.messaging.streams;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanCollectedEvent;
import com.univ.tracedin.domain.span.TraceId;
import com.univ.tracedin.infra.anomaly.client.AnomalyDetectionClient;

@Component
@EnableKafka
@EnableKafkaStreams
@RequiredArgsConstructor
public class AnomalyTraceStreamProcessor {

    @Value("${kafka.topic.anomaly-trace}")
    private String anomalyTraceTopic;

    private final AnomalyDetectionClient anomalyDetectionClient;

    void process(KStream<TraceId, SpanCollectedEvent> stream) {
        final JsonSerde<List<Span>> listOfSpanSerde = new JsonSerde<>(new TypeReference<>() {});

        // Stream processor - SpanCollectedEvent를 받아서 TraceId로 그루핑한뒤 각 TraceId에 대한 Span들을 List로 묶어서
        final KStream<TraceId, Span> spanStream =
                stream.flatMap(
                        (key, value) -> {
                            final List<KeyValue<TraceId, Span>> result = new ArrayList<>();
                            for (Span span : value.spans()) {
                                final TraceId traceId = span.getTraceId();
                                result.add(new KeyValue<>(traceId, span));
                            }
                            return result;
                        });

        final TimeWindows timeWindows =
                TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(30));

        // KTable로 만듦
        final KTable<Windowed<TraceId>, List<Span>> traceTable =
                spanStream
                        .groupByKey()
                        .windowedBy(timeWindows)
                        .aggregate(
                                ArrayList::new,
                                (key, value, aggregate) -> {
                                    aggregate.add(value);
                                    return aggregate;
                                },
                                Materialized.with(getTraceIdSerde(), listOfSpanSerde));

        traceTable
                .toStream()
                .map((windowedKey, spansList) -> KeyValue.pair(windowedKey.key(), spansList))
                .mapValues(anomalyDetectionClient::detect)
                .filter((key, result) -> result.isAnomaly())
                // Sink processor - 각 TraceId에 대한 Span들을 AnomalyDetectionClient로 전달하여 이상치 탐지를 수행하고
                // 결과가 이상치인 경우 anomaly-trace 토픽으로 전달
                .to(anomalyTraceTopic, Produced.keySerde(getTraceIdSerde()));
    }

    private static Serde<TraceId> getTraceIdSerde() {
        final JsonSerde<TraceId> traceIdSerde = new JsonSerde<>(TraceId.class);
        traceIdSerde.configure(Map.of(), true);
        return traceIdSerde;
    }
}
