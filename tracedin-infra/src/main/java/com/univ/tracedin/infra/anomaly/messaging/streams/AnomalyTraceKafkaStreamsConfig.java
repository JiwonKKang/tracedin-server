package com.univ.tracedin.infra.anomaly.messaging.streams;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

import com.univ.tracedin.domain.span.SpanCollectedEvent;
import com.univ.tracedin.domain.span.TraceId;

@Configuration
@RequiredArgsConstructor
public class AnomalyTraceKafkaStreamsConfig {

    @Value("${kafka.topic.span}")
    private String inputTopic;

    private final AnomalyTraceStreamProcessor spanStreamProcessor;

    @Bean
    public KStream<TraceId, SpanCollectedEvent> spanStreams(StreamsBuilder streamsBuilder) {
        final KStream<TraceId, SpanCollectedEvent> stream = streamsBuilder.stream(inputTopic);
        spanStreamProcessor.process(stream);
        return stream;
    }
}
