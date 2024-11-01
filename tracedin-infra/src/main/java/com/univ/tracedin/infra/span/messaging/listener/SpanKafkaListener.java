package com.univ.tracedin.infra.span.messaging.listener;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.univ.tracedin.domain.span.Span;
import com.univ.tracedin.domain.span.SpanAppender;
import com.univ.tracedin.domain.span.SpanCollectedEvent;
import com.univ.tracedin.domain.span.TraceId;
import com.univ.tracedin.infra.kafka.KafkaConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpanKafkaListener implements KafkaConsumer<TraceId, SpanCollectedEvent> {

    private final SpanAppender spanAppender;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.span-group-id}", topics = "${kafka.topic.span}")
    public void receive(List<ConsumerRecord<TraceId, SpanCollectedEvent>> records) {
        log.info(
                "{} number of span collected events received with keys:{}, partitions:{} and offsets: {}",
                records.size(),
                records.stream().map(ConsumerRecord::key).toList(),
                records.stream().map(ConsumerRecord::partition).toList(),
                records.stream().map(ConsumerRecord::offset).toList());

        final List<Span> spans =
                records.stream()
                        .map(ConsumerRecord::value)
                        .map(SpanCollectedEvent::spans)
                        .flatMap(List::stream)
                        .toList();

        spanAppender.appendAll(spans);
    }
}
